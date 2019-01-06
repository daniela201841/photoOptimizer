package ca.lotlinx.service;

import ca.lotlinx.config.Config;
import ca.lotlinx.dto.GenericReturn;
import ca.lotlinx.dto.StatusError;
import ca.lotlinx.dto.StatusType;
import ca.lotlinx.dto.ValuesStatus;
import com.google.gson.JsonArray;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class SubmitImageService implements Service<Boolean, List<String>> {

    private final ValueService valueService;

    private final RestService restService;

    private final Config config;

    SubmitImageService(ValueService valueService, RestService restService, Config config) {
        this.valueService = valueService;
        this.restService = restService;
        this.config = config;
    }

    /**
     * Send images to be processed and manage the flow of processing
     *
     * @param images url images list
     * @return Boolean indicanding sucess or not
     */
    public Boolean submit(List<String> images) {
        getLogger().info("Submiting images: " + images);

        JsonArray values = this.valueService.submit(images);
        GenericReturn response = restService.postOptimize(values);

        if (response != null && response.get() instanceof ValuesStatus) {
            getLogger().info("Submit images processed.");

            ValuesStatus responseValues = (ValuesStatus) response.get();
            //A complete request is finished, and can be called.
            if (StatusType.COMPLETE.equalsIgnoreCase(responseValues.getValues().getStatus())) {
                finalizeImageProcess(responseValues.getValues().getToken());
                return Boolean.TRUE;
                //A queued request is still waiting to be processed.
            } else if (StatusType.QUEUED.equalsIgnoreCase(responseValues.getValues().getStatus())) {
                 queueImageProcess(responseValues.getValues().getToken());
                 return Boolean.TRUE;
            }
        } else if (response != null && response.get() instanceof StatusError) {
            StatusError status = (StatusError) response.get();
            getLogger().info("Request failed. Server status: " + status.getStatus());
            getLogger().info("Issue:" + status.getMeta().getErrorMsg());

            //A failed request must be called again to restart.
            try {
                    TimeUnit.SECONDS.sleep(2);
                    submit(images);
            } catch (InterruptedException ex) {
                getLogger().log(Level.SEVERE, "Fail checking queued processing", ex);
            }
        }
        return Boolean.FALSE;
    }

    /**
     * Finalize images processing downloading images
     *
     * @param token identifier of the processing
     */
    private void finalizeImageProcess(String token) {
        getLogger().info("Request completed. Token: " + token);
        List<String> urls = restService.load(token);
        if (!urls.isEmpty()) {
            urls.forEach(url -> downloadImage(url));
        }
    }

    /**
     * Download the modified Image from modifiedUrl
     *
     * @param modifiedUrl new url with image modified
     */
    private void downloadImage(String modifiedUrl) {
        try {
            URL url = new URL(modifiedUrl);
            BufferedImage img = ImageIO.read(url);
            String fileName = modifiedUrl.substring(modifiedUrl.lastIndexOf('/') + 1, modifiedUrl.length());
            File file = new File(fileName);
            String format = fileName.substring(fileName.indexOf('.') + 1);
            ImageIO.write(img, format, file);

        } catch (IOException ex) {
            getLogger().log(Level.SEVERE, "Fail loading URL:" + modifiedUrl, ex);
        }
    }

    /**
     * Check status, if completed, call finalize process, else keep checking.
     *
     * @param token: identifier of the processing     *
     */
    private void  queueImageProcess(String token) {
        getLogger().info("A queued request is still waiting to be processed. Token: " + token);
        try {
                if (restService.verifyStatus(token)) {
                    //COMPLETED
                    finalizeImageProcess(token);
                } else {
                    TimeUnit.SECONDS.sleep(2);
                    queueImageProcess(token);
                }
        }catch(InterruptedException e) {
            getLogger().log(Level.SEVERE, "Fail checking queued processing", e);
        }

    }
}