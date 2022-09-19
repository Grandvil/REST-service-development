package com.example.LR1;

import com.google.gson.Gson;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stax.StAXSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

@SpringBootApplication
@RestController
public class Lr1Application {

    public static void main(String[] args) {
        SpringApplication.run(Lr1Application.class, args);
    }

    @GetMapping("/")
    public String first(@RequestParam(value = "name", defaultValue = "World") String name) throws IOException, JAXBException {
        File file = new File("src/main/resources/example2.xml");

        // Валидация
        boolean ok = schemaValidator();
        SystemXML systemXML = new SystemXML();

        if (ok) {
            JAXBContext jaxbContext = JAXBContext.newInstance(SystemXML.class);

            // Анмаршалинг
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            systemXML = (SystemXML) jaxbUnmarshaller.unmarshal(file);

            // Меняем объект
            systemXML.personList.personList.get(0).firstName = "Anton";
            systemXML.personList.personList.get(0).lastName = "Antonov";
            systemXML.personList.personList.get(0).pnoneNumber = "12345";
            systemXML.chatHistory.chatList.get(1).messageList.messageList.get(0).content.body = "NEW MESSAGE";

            // Создаём XML с измененными данными
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            jaxbMarshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "src/main/resources/XMLScheme.xsd");

            OutputStream os = new FileOutputStream(".\\myxml.xml");
            jaxbMarshaller.marshal(systemXML, os);

            try (FileWriter writer = new FileWriter("myjson.json", false)) {
                writer.write(new Gson().toJson(systemXML));
                writer.flush();
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
        return new Gson().toJson(systemXML);
    }

    public static boolean schemaValidator() {
        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new File(".\\src\\main\\resources\\XMLScheme.xsd"));
            Validator validator = schema.newValidator();
            XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(Files.newInputStream(Paths.get(".\\src\\main\\resources\\example2.xml")));
            validator.validate(new StAXSource(reader));

            System.out.println("XML is valid");
            return true;
        } catch (Exception e) {
            System.out.println("XML is not valid");
            return false;
        }
    }
}
