package com.example.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    @Autowired
    private DataSource dataSource;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("\n==============================================");
        System.out.println("DUOMENŲ BAZĖS INICIALIZACIJA");
        System.out.println("==============================================");

        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            
            System.out.println("\n✓ Prisijungta prie MySQL duomenų bazės");
            System.out.println("  • Duomenų bazė: " + metaData.getDatabaseProductName());
            System.out.println("  • Versija: " + metaData.getDatabaseProductVersion());
            System.out.println("  • URL: " + metaData.getURL());
            System.out.println("  • Vartotojas: " + metaData.getUserName());

            // Patikriname ar schema "minima" egzistuoja
            System.out.println("\n----------------------------------------------");
            System.out.println("Tikrinimas schemos 'minima'...");
            System.out.println("----------------------------------------------");
            
            ResultSet schemas = metaData.getCatalogs();
            boolean schemaExists = false;
            while (schemas.next()) {
                String schemaName = schemas.getString("TABLE_CAT");
                if ("minima".equalsIgnoreCase(schemaName)) {
                    schemaExists = true;
                    break;
                }
            }
            
            if (schemaExists) {
                System.out.println("✓ Schema 'minima' rasta");
            } else {
                System.out.println("✗ Schema 'minima' nerasta - ji bus sukurta automatiškai");
            }

            // Patikriname lentelių egzistavimą
            System.out.println("\n----------------------------------------------");
            System.out.println("Tikrinamos lentelės schemoje 'minima'...");
            System.out.println("----------------------------------------------");

            ResultSet tables = metaData.getTables("minima", null, "%", new String[]{"TABLE"});
            int tableCount = 0;
            System.out.println("\nRastos lentelės:");
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                System.out.println("  • " + tableName);
                tableCount++;
                
                // Parodome kiekvienos lentelės struktūrą
                ResultSet columns = metaData.getColumns("minima", null, tableName, null);
                while (columns.next()) {
                    String columnName = columns.getString("COLUMN_NAME");
                    String columnType = columns.getString("TYPE_NAME");
                    int columnSize = columns.getInt("COLUMN_SIZE");
                    String nullable = columns.getString("IS_NULLABLE");
                    System.out.println("      - " + columnName + " (" + columnType + 
                                     "(" + columnSize + "), " + 
                                     (nullable.equals("YES") ? "NULL" : "NOT NULL") + ")");
                }
            }
            
            if (tableCount == 0) {
                System.out.println("  (nėra lentelių - jos bus sukurtos automatiškai per Hibernate)");
            }

            System.out.println("\n==============================================");
            System.out.println("✓ INICIALIZACIJA BAIGTA SĖKMINGAI");
            System.out.println("==============================================");
            System.out.println("\nREST API ENDPOINTAI (8 iš viso):");
            System.out.println("  1. Parduotuvės:        http://localhost:8080/api/parduotuves");
            System.out.println("  2. Darbuotojai:        http://localhost:8080/api/darbuotojai");
            System.out.println("  3. Pareigos:           http://localhost:8080/api/pareigos");
            System.out.println("  4. Prekės:             http://localhost:8080/api/prekes");
            System.out.println("  5. Kategorijos:        http://localhost:8080/api/kategorijos");
            System.out.println("  6. Inventorius:        http://localhost:8080/api/inventorius");
            System.out.println("  7. Pardavimai:         http://localhost:8080/api/pardavimai");
            System.out.println("  8. Pardavimo eilutės:  http://localhost:8080/api/pardavimo-eilutes");
            System.out.println("\nFrontend:              http://localhost:8080");
            System.out.println("==============================================\n");

        } catch (Exception e) {
            System.err.println("\n==============================================");
            System.err.println("✗ KLAIDA INICIALIZUOJANT DUOMENŲ BAZĘ");
            System.err.println("==============================================");
            System.err.println("Klaidos pranešimas: " + e.getMessage());
            System.err.println("\nPatikrinkite:");
            System.err.println("  1. Ar MySQL serveris paleistas?");
            System.err.println("  2. Ar teisingi prisijungimo duomenys?");
            System.err.println("  3. Ar schema 'minima' egzistuoja?");
            System.err.println("==============================================\n");
            throw e;
        }
    }
}
