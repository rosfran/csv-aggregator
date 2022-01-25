package org.joinable.csv.aggregator.controller;

import org.joinable.csv.aggregator.config.ApplicationConfig;
import org.joinable.csv.aggregator.service.CSVAggregatorService;
import org.joinable.csv.aggregator.service.FilesService;
import org.joinable.csv.aggregator.util.FilesUtil;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.Dataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
//import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class CSVAggregatorController
{

    @Autowired
    private ApplicationConfig config;

    private final CSVAggregatorService csvAggregatorService;

    private final FilesService filesService;

    @Autowired
    public CSVAggregatorController(CSVAggregatorService csvAggregatorService, FilesService filesService)
    {
        this.csvAggregatorService = csvAggregatorService;
        this.filesService = filesService;
    }

    @GetMapping("/files")
    @ResponseStatus(HttpStatus.OK)
    public List<File> getFiles(@PathVariable("type") String fType)
    {

        if (fType.equalsIgnoreCase("small"))
        {
            return filesService.getAllSmallFiles();
        }
        else if (fType.equalsIgnoreCase("medium"))
        {
            return filesService.getAllMediumFiles();
        }
        else
        {
            return filesService.getAllFiles();
        }
    }

    @GetMapping("/all-files")
    @ResponseStatus(HttpStatus.OK)
    public List<File> getAllFiles()
    {

        return filesService.getAllFiles();
    }

    @GetMapping("/medium-files")
    @ResponseStatus(HttpStatus.OK)
    public List<File> getAllMediumFiles()
    {

        return filesService.getAllMediumFiles();
    }

    @GetMapping("/small-files")
    @ResponseStatus(HttpStatus.OK)
    public List<File> getAllSmallFiles()
    {

        return filesService.getAllSmallFiles();
    }

    @PutMapping("/aggregated-csv")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> aggregateMediumCSV(@RequestParam(
            value = "type",
            required = true,
            defaultValue = "0") String fType)
    {
        Dataset<Row> dt = null;

        if ( fType.equalsIgnoreCase("medium"))
        {
            dt = csvAggregatorService.aggregateMediumCSV("result_medium.csv");
        } else if ( fType.equalsIgnoreCase("small")) {
            dt = csvAggregatorService.aggregateSmallCSV("result_small.csv");
        }
        return ResponseEntity.ok(dt);

    }

    @PostMapping("/files")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> sendFile(
            @RequestParam("csv-files") MultipartFile[] files)
    {
        if (FilesUtil.copyFilesToDir(files, config.getDirVolume()))
        {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
        else
        {
            return ResponseEntity.badRequest().build();
        }
    }



}
