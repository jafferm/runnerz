package com.spring.first.runnerz.run;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Repository
public class RunRepository {

    private static final Logger log = LoggerFactory.getLogger(RunRepository.class);
    private final JdbcClient jdbcClient;

    public RunRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public List <Run> findAll(){
        return jdbcClient.sql("select * from run")
                .query(Run.class)
                .list();
    }

    public Optional <Run> findById(Integer id){
        return jdbcClient.sql("select id,title, started_on,completed_on,miles,location from run where id= :id")
                .param("id",id)
                .query(Run.class)
                .optional();
    }

    public void Create(Run run) {
        String sql = "INSERT INTO run (id,title, started_on, completed_on, miles, location) VALUES (:id, :title, :started_on, :completed_on, :miles, :location)";
        var updated= jdbcClient.sql(sql)
                .param("id",run.id())
                .param("title", run.title())
                .param("started_on", run.startedOn())
                .param("completed_on", run.completedOn())
                .param("miles", run.miles())
                .param("location", run.location().toString())
                .update();
        Assert.state(updated ==1,"failed to create run: "+run.title());
    }


    public void update(Run run, Integer id) {
        String sql = "UPDATE run SET title = :title, started_on = :started_on, completed_on = :completed_on, miles = :miles, location = :location WHERE id = :id";
        var updated = jdbcClient.sql(sql)
                .param("title", run.title())
                .param("started_on", run.startedOn())
                .param("completed_on", run.completedOn())
                .param("miles", run.miles())
                .param("location", run.location().toString())
                .param("id", id)
                .update();
        Assert.state(updated == 1, "Failed to update run with id: " + id);
    }


    public void delete(Integer id) {
        String sql = "DELETE FROM run WHERE id = :id";
        var updated = jdbcClient.sql(sql)
                .param("id", id)
                .update();
        Assert.state(updated == 1, "Failed to delete run with id: " + id);
    }

    public void saveAll(List<Run> runs) {
        runs.stream().forEach(this::Create);
    }

    public List<Run> findByLocation(String location) {
        return jdbcClient.sql("select * from run where location = :location")
                .param("location", location)
                .query(Run.class)
                .list();
    }

    public int count() {
        return jdbcClient.sql("select * from run").query().listOfRows().size();
    }

//    @PostConstruct
//    private void init() {
//        runs.add(new Run(1,
//                "Monday Morning Run",
//                LocalDateTime.now(),
//                LocalDateTime.now().plus(30, ChronoUnit.MINUTES),
//                3,
//                Location.INDOOR));
//
//        runs.add(new Run(2,
//                "Wednesday Evening Run",
//                LocalDateTime.now(),
//                LocalDateTime.now().plus(60, ChronoUnit.MINUTES),
//                6,
//                Location.INDOOR));
//    }
}
