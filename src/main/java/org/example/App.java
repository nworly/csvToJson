package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.List;


public class App {
    public static void main(String[] args) {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String csvFileName = "data.csv";
        String jsonFileName = "data.json";

        List<Employee> list = parseCSV(csvFileName, columnMapping, Employee.class);
        String json = listToJson(list);
        writeString(json, jsonFileName);
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
