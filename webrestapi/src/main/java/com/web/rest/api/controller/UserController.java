package com.web.rest.api.controller;

import com.web.rest.api.controller.model.UserModelSearch;
import com.web.rest.api.exception.ResourceNotFoundException;
import com.web.rest.api.help.SearchCriteria;
import com.web.rest.api.model.UserModel;
import com.web.rest.api.repository.UserRepository;
import com.web.rest.api.repository.UserSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping(path = "/user")
@CrossOrigin
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping(path = "")
    public List<UserModel> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<UserModel> getUserById(@PathVariable("id") Integer id) throws ResourceNotFoundException {
        UserModel user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for this id :: " + id));
        return ResponseEntity.ok().body(user);
    }

    @PostMapping(path = "/save")
    public UserModel save(@Validated @RequestBody UserModel user) {
        return userRepository.save(user);
    }

    @PutMapping(path = "/update/{id}")
    public ResponseEntity<UserModel> updateUser(@PathVariable(value = "id") Integer id,
                                                @Validated @RequestBody UserModel userDetails) throws ResourceNotFoundException {
        UserModel user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for this id :: " + id));

        user.setUsername(userDetails.getUsername());
        user.setPassword(userDetails.getPassword());
        user.setEnabled(userDetails.isEnabled());
        user.setRegisterDate(userDetails.getRegisterDate());
        user.setName(userDetails.getName());
        user.setSurname(userDetails.getSurname());
        user.setEmail(userDetails.getEmail());
        user.setPhone(userDetails.getPhone());
        final UserModel updatedUser = userRepository.save(user);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping(path = "/{id}")
    public Map<String, Boolean> deleteUser(@PathVariable(value = "id") Integer id) throws ResourceNotFoundException {
        UserModel user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for this id :: " + id));

        userRepository.delete(user);
        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);
        return response;
    }

    @GetMapping(path = "/search")
    public ResponseEntity<UserModelSearch> filters(@RequestParam MultiValueMap<String, String> filters) {
        List<String> search = filters.get("search");
        UserSpecification specName = new UserSpecification(new SearchCriteria("name", ":", null));
        UserSpecification specUsername = new UserSpecification(new SearchCriteria("username", ":", null));
        UserSpecification specEmail = new UserSpecification(new SearchCriteria("email", ":", null));
        filters.get("filter").forEach(element -> {
            List<String> filter = Arrays.asList(element.split(","));
            filter.forEach(key -> {
                if(key.equals("name")){
                    specName.setCriteria(new SearchCriteria("name", ":", search.get(0).toUpperCase()));
                }else if(key.equals("username")){
                    specUsername.setCriteria(new SearchCriteria("username", ":", search.get(0).toUpperCase()));
                }else if(key.equals("email")){
                    specEmail.setCriteria(new SearchCriteria("email", ":", search.get(0).toUpperCase()));
                }
            });
        });
        List<UserModel> results = new ArrayList<>();
        if(specName.getCriteria().getValue() != null && specUsername.getCriteria().getValue() != null && specEmail.getCriteria().getValue() != null){
            results = userRepository.findAll(Specification.where(specName).or(specUsername).or(specEmail));
        } else if(specName.getCriteria().getValue() != null && specUsername.getCriteria().getValue() != null){
            results = userRepository.findAll(Specification.where(specName).or(specUsername));
        } else if(specUsername.getCriteria().getValue() != null && specEmail.getCriteria().getValue() != null){
            results = userRepository.findAll(Specification.where(specUsername).or(specEmail));
        } else if(specName.getCriteria().getValue() != null && specEmail.getCriteria().getValue() != null){
            results = userRepository.findAll(Specification.where(specName).or(specEmail));
        } else if(specName.getCriteria().getValue() != null){
            results = userRepository.findAll(Specification.where(specName));
        } else if(specUsername.getCriteria().getValue() != null){
            results = userRepository.findAll(Specification.where(specUsername));
        } else if(specEmail.getCriteria().getValue() != null){
            results = userRepository.findAll(Specification.where(specEmail));
        }
        UserModelSearch result = new UserModelSearch(results, results.size());
        return ResponseEntity.ok().body(result);
    }
}
