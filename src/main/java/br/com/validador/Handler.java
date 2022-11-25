package br.com.validador;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.Arrays;

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

      String[] tipos = System.getenv().get("tipos").split(",");
      var tipoObjeto = nomeObjeto.split("\\.")[1].toUpperCase();
      boolean valido = Arrays.stream(tipos).anyMatch(tipoObjeto::equals);
        if (!valido){
            try{
                AmazonS3 s3Cliente = AmazonS3ClientBuilder.defaultClient();
                DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucket, nomeObjeto);
                s3Cliente.deleteObject(deleteObjectRequest);
                logger.log("============ Objeto Inválido=============");
                logger.log("Objeto excluído com sucesso!");
                return "Arquivo" + nomeObjeto + "excluido com sucesso";
            } catch (Exception e){
                logger.log(e.getMessage());
                throw new RuntimeException();
            }
        }
        logger.log("============ Objeto Válido=============");
        return "Arquivo" + nomeObjeto + "validado";
    }
}
