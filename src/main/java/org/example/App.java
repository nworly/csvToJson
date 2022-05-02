package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class App {
    public static void main(String[] args) {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String csvFileName = "data.csv";
        String csvToJsonFileName = "data.json";

        List<Employee> list1 = parseCSV(csvFileName, columnMapping, Employee.class);
        String json = listToJson(list1);
        writeString(json, csvToJsonFileName);

        String xmlFileName = "data.xml";
        String xmlToJsonFileName = "data2.json";

        List<Employee> list2 = parseXML(xmlFileName);
        writeString(listToJson(list2), xmlToJsonFileName);
    }

    private static List<Employee> parseXML(String xmlFileName) {
        List<Employee> employees = new ArrayList<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(xmlFileName));

            Node root = document.getDocumentElement();
            NodeList nodeList1 = root.getChildNodes();

            int employeeListNumber = -1;

            for (int i = 0; i < nodeList1.getLength(); i++) {
                Node node1 = nodeList1.item(i);
                if (Node.ELEMENT_NODE == node1.getNodeType()) {
                    NodeList nodeList2 = node1.getChildNodes();
                    employees.add(new Employee());
                    employeeListNumber++;

                    for (int j = 0; j < nodeList2.getLength(); j++) {
                        Node node2 = nodeList2.item(j);
                        if (Node.ELEMENT_NODE == node2.getNodeType()) {
                            Element element = (Element) node2;
                            String name = element.getNodeName();
                            String value = element.getTextContent();

                            switch (name) {
                                case "id":
                                    employees.get(employeeListNumber).setId(Long.parseLong(value));
                                    break;
                                case "firstName":
                                    employees.get(employeeListNumber).setFirstName(value);
                                    break;
                                case "lastName":
                                    employees.get(employeeListNumber).setLastName(value);
                                    break;
                                case "country":
                                    employees.get(employeeListNumber).setCountry(value);
                                    break;
                                case "age":
                                    employees.get(employeeListNumber).setAge(Integer.parseInt(value));
                                    break;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return employees;
    }

    private static void writeString(String json, String fileName) {
        try (Writer writer = new FileWriter(fileName)) {
            writer.write(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static <T> String listToJson(List<T> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Type listType = new TypeToken<List<T>>() {
        }.getType();
        return gson.toJson(list, listType);
    }

    public static <T> List<T> parseCSV(String file, String[] columnMapping, Class<T> tClass) {
        ColumnPositionMappingStrategy<T> strategy = new ColumnPositionMappingStrategy<>();
        strategy.setType(tClass);
        strategy.setColumnMapping(columnMapping);
        List<T> list = null;

        try (CSVReader reader = new CSVReader(new FileReader(file))) {
            CsvToBean<T> csv = new CsvToBeanBuilder<T>(reader)
                    .withMappingStrategy(strategy)
                    .build();

            list = csv.parse();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
