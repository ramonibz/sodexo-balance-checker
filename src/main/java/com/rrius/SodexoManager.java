package com.rrius;

import com.rrius.http.SodexoHttpClient;
import com.rrius.http.parsers.SodexoCardDetailParser;
import com.rrius.http.parsers.SodexoTransactionsParser;
import com.rrius.model.SodexoCard;
import com.rrius.model.Transaction;
import com.rrius.notifications.Notifier;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class SodexoManager  implements Runnable{


  private final Logger logger = LoggerFactory.getLogger(SodexoManager.class);

  private static final String DEFAULT_PROPERTIES_NAME = "app.properties";

  private SodexoHttpClient sodexoHttpClient;

  private String dataFile = "data/data.yml";
  private String propertiesFile;

  private String userName;
  private String password;
  private String cardId;
  private String pushbulletAccessToken;

  private Notifier notifier;

  public SodexoManager(SodexoHttpClient sodexoHttpClient, Notifier notifier) {
    this(sodexoHttpClient, notifier, DEFAULT_PROPERTIES_NAME);
  }

  public SodexoManager(SodexoHttpClient sodexoHttpClient, Notifier notifier, String propertiesFile) {
    this.notifier = notifier;
    this.sodexoHttpClient = sodexoHttpClient;
    this.propertiesFile = propertiesFile;
    loadProperties();
  }


  private void loadProperties() {
    Parameters params = new Parameters();
    FileBasedConfigurationBuilder<FileBasedConfiguration> builder =
        new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
            .configure(params.properties()
                .setFileName(this.propertiesFile));
    try {
      Configuration config = builder.getConfiguration();

      this.userName = config.getString("sodexo.username");
      this.password = config.getString("sodexo.password");
      this.cardId = config.getString("sodexo.card_id");
      this.pushbulletAccessToken = config.getString("pushbullet.token");
      if (this.userName==null || this.userName.trim().length()==0){
        throw new RuntimeException("sodexo.username is a required property");
      }
      if (this.password==null || this.password.trim().length()==0){
        throw new RuntimeException("sodexo.password is a required property");
      }
      if (this.cardId==null || this.cardId.trim().length()==0){
        throw new RuntimeException("sodexo.card_id is a required property");
      }
      if (this.pushbulletAccessToken==null || this.pushbulletAccessToken.trim().length()==0){
        throw new RuntimeException("pushbullet.token is a required property");
      }
    }
    catch(ConfigurationException cex) {
      throw new RuntimeException("Error loading properties configuration", cex);
    }
  }


  @Override
  public void run(){
    execute();
  }

  public void execute() {
    this.logger.debug("Starting process.");

    login();

    SodexoCard sodexoServerCard = getSodexoServerCard();
    SodexoCard sodexoLocalCard = getSodexoLocalCard();

    if (!sodexoLocalCard.equals(sodexoServerCard)) {
      this.logger.debug("Server card and local card do not match. Updating local data. .  .");

      //update local data with server card info
      updateLocalCard(sodexoServerCard);

    }
    this.logger.info("Process done.");
  }


  public void login() {
    this.sodexoHttpClient.tryLogin(this.userName, this.password);
  }

  public SodexoCard getSodexoServerCard() {
    Document document = this.sodexoHttpClient.getCardDetailDocument(this.cardId);
    SodexoCardDetailParser sodexoParser = new SodexoCardDetailParser(document);
    Double balance = sodexoParser.getCurrentBalance(this.cardId);
    String encryptedCardId = sodexoParser.getEncryptedCardId(this.cardId);
    String encryptedCardNumber = sodexoParser.getEncryptedCardNumber(this.cardId);
    Transaction lastTransaction = retrieveLastTransaction(encryptedCardId, encryptedCardNumber);
    SodexoCard sodexoCard = new SodexoCard(this.cardId, balance, lastTransaction);
    if(this.logger.isDebugEnabled()) {
      this.logger.debug("Sodexo Card: " + sodexoCard.toString());
    }
    return sodexoCard;
  }

  protected Transaction retrieveLastTransaction(String encryptedCardId, String encrypteddCardNumber) {
    Document document = this.sodexoHttpClient.getLastTransactionsDocument(encrypteddCardNumber, encryptedCardId);
    SodexoTransactionsParser sodexoTransactionsParser = new SodexoTransactionsParser(document);
    return sodexoTransactionsParser.getLastTransaction();
  }

  public SodexoCard getSodexoLocalCard () {
    try {
      FileInputStream dataInputStream = new FileInputStream(this.getDataFile());
      Yaml yml = new Yaml();
      return yml.loadAs(dataInputStream, SodexoCard.class);
    } catch (FileNotFoundException e){
      throw new RuntimeException("Error Loading local datafile '" + this.getDataFile() + "'", e);
    }
  }

  public void updateLocalCard(SodexoCard sodexoCard) {
    Yaml yaml = new Yaml();
    String cardDump = yaml.dump(sodexoCard);
    BufferedWriter bufferedWriter = null;
    try {
      bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.getDataFile())));
      bufferedWriter.write(cardDump);
    } catch (IOException e) {
      throw new RuntimeException("Error writing to data file", e);
    } finally {
      if (bufferedWriter != null) {
        try{
          bufferedWriter.close();
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
    }
  }

  public String getDataFile() {
    return dataFile;
  }

  public void setDataFile(String dataFile) {
    this.dataFile = dataFile;
  }

}
