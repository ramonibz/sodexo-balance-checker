package com.rrius;

import com.rrius.http.SodexoHttpClient;
import com.rrius.model.SodexoCard;
import com.rrius.model.Transaction;
import com.rrius.notifications.Notifier;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;


public class SodexoManagerTest {

  private SodexoManager sodexoManager;
  @Mock
  private SodexoHttpClient sodexoHttpClient;
  @Mock
  private Notifier notifier;

  private Document cardDetailDocument;
  private Document lastTransactionsDocument;

  private Transaction sampleTransaction;
  private SodexoCard sampleSodexoCard;

  @Before
  public void setUp() throws Exception{

    MockitoAnnotations.initMocks(this);
    this.sodexoManager = new SodexoManager(this.sodexoHttpClient, this.notifier, "app-test.properties");
    this.sodexoManager.setDataFile("data/data-test.yml");


    InputStream cardDetailInputStream = this.getClass().getClassLoader().getResourceAsStream("cardDetailDocument.html");
    this.cardDetailDocument = Jsoup.parse(cardDetailInputStream, StandardCharsets.UTF_8.toString(), "http://www.mysodexo.es/");

    InputStream lastTransactionsInputStream = this.getClass().getClassLoader().getResourceAsStream("lastTransactionsDocument.html");
    this.lastTransactionsDocument = Jsoup.parse(lastTransactionsInputStream, StandardCharsets.UTF_8.toString(), "http://www.mysodexo.es/");

    this.sampleTransaction = new Transaction("Pago en SERUNION, S.A.-INM. CO,SANT CUGAT D", "27/07/2016", -4.3);
    this.sampleSodexoCard = new SodexoCard("75297", 5.22, this.sampleTransaction);

    copyFiles(this.sodexoManager.getDataFile(), this.sodexoManager.getDataFile() + ".bk");

  }

  @After
  public void tearDown() throws Exception{
    copyFiles(this.sodexoManager.getDataFile() + ".bk", this.sodexoManager.getDataFile());
    new File(this.sodexoManager.getDataFile() + ".bk").delete();
  }

  @Test
  public void testGetSodexoServerCard() throws Exception {
    Mockito.when(this.sodexoHttpClient.getLastTransactionsDocument(Mockito.anyString(), Mockito.anyString()))
        .thenReturn(this.lastTransactionsDocument);
    Mockito.when(this.sodexoHttpClient.getCardDetailDocument(Mockito.anyString()))
        .thenReturn(this.cardDetailDocument);

    SodexoCard sodexoCard = this.sodexoManager.getSodexoServerCard();

    Assert.assertTrue(sodexoCard.equals(this.sampleSodexoCard));
  }

  @Test
  public void testGetSodexoLocalCard() {
    SodexoCard sodexoLocalCard = this.sodexoManager.getSodexoLocalCard();

    Assert.assertTrue(sodexoLocalCard.equals(this.sampleSodexoCard));
  }

  @Test
  public void testUpdateLocalCard() throws  Exception{
    FileInputStream dataInputStream = new FileInputStream(this.sodexoManager.getDataFile());
    Yaml yml = new Yaml();
    SodexoCard sodexoLocalCard = yml.loadAs(dataInputStream, SodexoCard.class);

    this.sodexoManager.updateLocalCard(this.sampleSodexoCard);

    Assert.assertTrue(sodexoLocalCard.equals(this.sampleSodexoCard));

  }

  private void copyFiles(String source, String dest) throws Exception {

    FileChannel sourceChannel = null;
    FileChannel destChannel = null;
    try {
      sourceChannel = new FileInputStream(source).getChannel();
      destChannel = new FileOutputStream(dest).getChannel();
      destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
    }catch (Exception e) {
      throw new Exception("Error copying file '" + source + "' to '" + dest + "'", e);
    }finally{
      sourceChannel.close();
      destChannel.close();
    }
  }

}