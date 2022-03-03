package com.web.rest.api.controller.model;

import com.web.rest.api.model.UserModel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UserModelSearch {
    private List<UserModel> results;
    private Integer total;
}
