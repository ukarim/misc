import java.io.FileInputStream;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;

public class JMdictTool {

  private static final String HTML_HEADER = """
      <meta charset="utf-8">
      <style>
      body {
        padding: 0;
        margin: 0 auto;
        width: 1240px;
        font-size: 16px;
        display: grid;
        grid-template-columns: repeat(4, 1fr);
      }
      div {
        box-sizing: border-box;
        text-align: center;
        padding: 0.5em;
      }
      div > * {
        display: block;
      }
      b {
        font-size: 2.5em;
      }
      </style>
      """;

  public static void main(String[] args) throws Exception {
    if (args.length != 1) {
      System.out.println("Usage: java JmdictTool.java JMdict_e.xml");
      System.exit(1);
    }

    String jmdictXmlFile = args[0];

    var dict = new HashMap<String, String>();

    var xif = XMLInputFactory.newInstance();
    xif.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
    xif.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, false);
    xif.setProperty(XMLInputFactory.SUPPORT_DTD, false);
    var xer = xif.createXMLEventReader(new FileInputStream(jmdictXmlFile));
    while (xer.hasNext()) {
      var xe = xer.nextEvent();
      if (xe.isStartElement()) {
        var se = xe.asStartElement();
        if ("entry".equals(se.getName().getLocalPart())) {
          parseEntry(xer, dict);
        }
      }
    }

    try (var out = new FileWriter("kyoiku_kanji_freq.html")) {
      out.write(HTML_HEADER);
      for (var line : Files.readAllLines(Paths.get("kyoiku_kanji_freq.txt"))) {
        String word = line.trim();
        String translation = dict.get(word);
        writeCard(out, word, translation);
      }
    }

    try (var out = new FileWriter("words_freq.html")) {
      out.write(HTML_HEADER);
      for (var line : Files.readAllLines(Paths.get("words_freq.txt"))) {
        String word = line.trim();
        String translation = dict.get(word);
        writeCard(out, word, translation);
      }
    }
  }

  private static void parseEntry(XMLEventReader xer, Map<String, String> dict) throws Exception {
    var jpWords = new ArrayList<String>();
    var readings = new ArrayList<String>();
    var meanings = new ArrayList<String>();
    while (xer.hasNext()) {
      var e = xer.nextEvent();
      if (e.isStartElement()) {
        var ese = e.asStartElement();
        switch (ese.getName().getLocalPart()) {
          case "k_ele" -> parseWords(xer, "keb", "k_ele", jpWords);
          case "r_ele" -> parseWords(xer, "reb", "r_ele", readings);
          case "sense" -> parseWords(xer, "gloss", "sense", meanings);
          default -> {
            // do nothing
          }
        }
      } else if (e.isEndElement()) {
        if ("entry".equals(e.asEndElement().getName().getLocalPart())) {
          // exit from entry parsing
          break;
        }
      }
    }
    String withoutReadings = String.join(", ", meanings);
    readings.add(withoutReadings);
    String withReadings = String.join(", ", readings);
    jpWords.forEach(w -> {
      dict.merge(w, withReadings, (old, ne_w) -> String.join(", ", old, ne_w));
    });
    readings.forEach(w -> {
      dict.merge(w, withoutReadings, (old, ne_w) -> String.join(", ", old, ne_w));
    });
  }

  private static void parseWords(XMLEventReader xer, String targetTag, String exitTag, List<String> acc) throws Exception {
    while (xer.hasNext()) {
      var e = xer.nextEvent();
      if (e.isStartElement()) {
        var se = e.asStartElement();
        if (targetTag.equals(se.getName().getLocalPart())) {
          // use only elements without any attributes
          if (!se.getAttributes().hasNext()) {
            acc.add(xer.nextEvent().asCharacters().getData());
          }
        }
      } else if (e.isEndElement()) {
        if (exitTag.equals(e.asEndElement().getName().getLocalPart())) {
          // exit from k_ele parsing
          break;
        }
      }
    }
  }

  private static void writeCard(FileWriter writer, String word, String translation) throws Exception {
    writer.write("<div>\n  <b>");
    writer.write(word);
    writer.write("</b>\n  <i>");
    if (translation != null) {
      writer.write(translation);
    }
    writer.write("</i>\n</div>\n");
  }
}
