package br.com.devcave.mybank.configuration;

import com.github.javafaker.Faker;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;

@Slf4j
public class FakerConfiguration {

    public static final Faker FAKER;

    static {
        final Long seed = System.currentTimeMillis();
        log.info("Faker seed {}", seed);
        FAKER = new Faker(new Random(seed));
    }

}
