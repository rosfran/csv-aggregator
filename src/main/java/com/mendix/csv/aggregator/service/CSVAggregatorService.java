package com.mendix.csv.aggregator.service;


import com.mendix.csv.aggregator.config.ApplicationConfig;
import com.mendix.csv.aggregator.util.FilesUtil;
import org.apache.commons.io.FileUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.functions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@Component
public class CSVAggregatorService
{

    //Logger logger = LoggerFactory.getLogger(CSVAggregatorService.class);

    Logger logger = LoggerFactory.getLogger(CSVAggregatorService.class);

    @Autowired
    private ApplicationConfig config;

    SparkConf conf;

    Dataset<Row> resultSpark;

    @Autowired
    private SparkSession sparkSession;

    @Autowired
    private JavaSparkContext javaSparkContext;

    public void stopSpark()
    {
        if (sparkSession != null)
        {
            sparkSession.stop();
        }
    }

    public void aggregateCSV( String arqOut )
    {


        resultSpark.repartition(50).orderBy(functions.asc("_c1")).coalesce(1).write().format("csv").csv(config.getResourcesDir()+arqOut );

    }

    public Dataset<Row> aggregateMediumCSV( String arqOut )
    {

        long st = System.currentTimeMillis();

        logger.info("Starting aggregateMediumCSV... = "+st);
        Dataset<Row> res =  sparkSession.read().format("csv").option("header", "false").load(config.getMediumFilesDir()+"/*").
                repartition(20).
                orderBy(functions.asc("_c0"));

        res.explain(true);
        res.printSchema();
        logger.info("list.size() == "+res.collectAsList().size());
        FilesUtil.deleteIfExists(config.getDirVolume()+"/out/tmp_"+arqOut+"t");

        res.coalesce(1).write().format("csv").mode("overwrite").save(config.getDirVolume()+"/out/tmp_"+arqOut+"t" );

        FilesUtil.copyToBaseDir(config.getDirVolume()+"/out/tmp_"+arqOut+"t", config.getDirVolume()+"/out/"+arqOut);


        try
        {
            FileUtils.deleteDirectory( new File(config.getDirVolume()+"/out/tmp_"+arqOut+"t")  );
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        logger.info("Stopping aggregateMediumCSV... = "+(System.currentTimeMillis() - st ) / 1000 );
        return res;

    }



}
