package com.web.rest.api.controller;

import com.web.rest.api.help.SearchCriteria;
import com.web.rest.api.model.UserModel;
import com.web.rest.api.repository.UserRepository;
import com.web.rest.api.repository.UserSpecification;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.domain.Specification;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

@DataJpaTest
class UserControllerTest {

    @Autowired
    private UserRepository repository;

    private UserModel userFirst;
    private UserModel userSecond;

    @BeforeEach
    void setUp() {
        userFirst = new UserModel();
        userFirst.setUsername("romario");
        userFirst.setPassword("pass");
        userFirst.setEnabled(true);
        userFirst.setRegisterDate(new GregorianCalendar(2000, Calendar.AUGUST, 28).getTime());
        userFirst.setName("Romário");
        userFirst.setSurname("Vargas");
        userFirst.setEmail("teste@teste.com");
        userFirst.setPhone("55 048998334259");
        UserModel resultFirst = repository.save(userFirst);
        userFirst.setId(resultFirst.getId());
        userSecond = new UserModel();
        userSecond.setUsername("jardiany");
        userSecond.setPassword("word");
        userSecond.setEnabled(true);
        userSecond.setRegisterDate(new GregorianCalendar(2000, Calendar.FEBRUARY, 23).getTime());
        userSecond.setName("Jardiany");
        userSecond.setSurname("Vargas");
        userSecond.setEmail("teste2@teste2.com");
        userSecond.setPhone("55 048999999999");
        UserModel resultSecond = repository.save(userSecond);
        userSecond.setId(resultSecond.getId());
    }

    @AfterEach
    void tearDown() {
        UserModel user = repository.findById(userFirst.getId()).orElse(null);
        repository.delete(user);
        user = repository.findById(userSecond.getId()).orElse(null);
        repository.delete(user);    }

    @Test
    void filters_with_two_surname() {
        UserSpecification spec =
                new UserSpecification(new SearchCriteria("surname", ":", "Vargas"));
        List<UserModel> results = repository.findAll(Specification.where(spec));

        Assertions.assertThat(results.contains(userFirst));
        Assertions.assertThat(results.contains(userSecond));
    }

    @Test
    void filters_with_email() {
        UserSpecification spec =
                new UserSpecification(new SearchCriteria("email", ":", "teste@teste.com"));
        List<UserModel> results = repository.findAll(Specification.where(spec));

        Assertions.assertThat(results.contains(userFirst));
        Assertions.assertThat(!results.contains(userSecond));
    }

    @Test
    void filters_with_name() {
        UserSpecification spec =
                new UserSpecification(new SearchCriteria("name", ":", "Romário"));
        List<UserModel> results = repository.findAll(Specification.where(spec));

        Assertions.assertThat(results.contains(userFirst));
        Assertions.assertThat(!results.contains(userSecond));
    }

    @Test
    void filters_with_username() {
        UserSpecification spec =
                new UserSpecification(new SearchCriteria("username", ":", "jardiany"));
        List<UserModel> results = repository.findAll(Specification.where(spec));

        Assertions.assertThat(results.contains(userFirst));
        Assertions.assertThat(!results.contains(userSecond));
    }

    @Test
    void filters_with_name_username_email() {
        UserSpecification specName =
                new UserSpecification(new SearchCriteria("name", ":", "Romário"));
        UserSpecification specUsername =
                new UserSpecification(new SearchCriteria("username", ":", "romario"));
        UserSpecification specEmail =
                new UserSpecification(new SearchCriteria("email", ":", "teste@teste.com"));
        List<UserModel> results = repository.findAll(Specification.where(specName).or(specUsername).or(specEmail));

        Assertions.assertThat(results.contains(userFirst));
        Assertions.assertThat(!results.contains(userSecond));
    }
}