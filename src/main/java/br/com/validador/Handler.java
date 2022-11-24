package br.com.validador;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Handler implements RequestHandler<S3Event, String> {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public String handleRequest(S3Event event, Context context) {
        LambdaLogger logger = context.getLogger();
      var record = event.getRecords().get(0);
      String nomeObjeto = record.getS3().getObject().getUrlDecodedKey();
      String bucket = record.getS3().getBucket().getName();

      logger.log("Objeto: " + nomeObjeto);
      logger.log("Bucket: " + bucket);

        return "ok";
    }
}
