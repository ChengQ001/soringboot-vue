package com.chengq.app.implcontroller;

import com.chengq.api.controller.UserController;
import com.chengq.api.model.DeleteUserRequest;
import com.chengq.api.model.UpdateUserRequest;
import com.chengq.api.model.UserRequest;
import com.chengq.api.model.base.ApiResponse;
import com.chengq.app.service.interfaces.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class UserControllerImpl implements UserController {

    @Autowired
    private UserService userService;

    @Override
    public ApiResponse<List<String>> getUsers(UserRequest request) {
        return ApiResponse.success(userService.getUsers(request));
    }

    @Override
    public ApiResponse<List<String>> getUsersAnonymous(UserRequest request) {
        return ApiResponse.success(userService.getUsers(request));
    }

    @Override
    public ApiResponse<List<String>> getUsersAdminOnly(UserRequest request) {
        return ApiResponse.success(userService.getUsers(request));
    }

    @Override
    public ApiResponse<List<String>> getUsersAdminOrManager(UserRequest request) {
        return ApiResponse.success(userService.getUsers(request));
    }

    @Override
    public ApiResponse<List<String>> getUsersWithReadPermission(UserRequest request) {
        return ApiResponse.success(userService.getUsers(request));
    }

    @Override
    public ApiResponse<List<String>> getUsersWithAnyPermission(UserRequest request) {
        return ApiResponse.success(userService.getUsers(request));
    }

    @Override
    public ApiResponse<List<String>> getUsersSecuredAdmin(UserRequest request) {
        return ApiResponse.success(userService.getUsers(request));
    }

    @Override
    public ApiResponse<List<String>> getUsersRolesAllowed(UserRequest request) {
        return ApiResponse.success(userService.getUsers(request));
    }

    @Override
    public ApiResponse<List<String>> getUsersPermitAll(UserRequest request) {
        return ApiResponse.success(userService.getUsers(request));
    }

    @Override
    public ApiResponse<List<String>> getUsersDenyAll(UserRequest request) {
        return ApiResponse.success(userService.getUsers(request));
    }

    @Override
    public ApiResponse<UserRequest> getUserPostAuthorize(UserRequest request) {
        return ApiResponse.success(request);
    }

    @Override
    public ApiResponse<List<UserRequest>> filterUsersPre(List<UserRequest> users) {
        return ApiResponse.success(users);
    }

    @Override
    public ApiResponse<List<String>> filterUsersPost(UserRequest request) {
        return ApiResponse.success(userService.getUsers(request));
    }

    @Override
    public ApiResponse<UserDetails> getCurrentUser(UserDetails userDetails) {
        return ApiResponse.success(userDetails);
    }

    @Override
    public ApiResponse<UserRequest> getUserById(UserRequest request) {
        return ApiResponse.success(request);
    }

    @Override
    public ApiResponse<String> getUsersWithCustomPermission(UserRequest request) {
        return ApiResponse.success("Custom permission granted");
    }

    @Override
    public ApiResponse<String> createUser(UserRequest request) {
        return ApiResponse.success(userService.createUser(request));
    }

    @Override
    public ApiResponse<String> deleteUser(DeleteUserRequest request) {
        return ApiResponse.success(userService.deleteUser(request));
    }
    

}