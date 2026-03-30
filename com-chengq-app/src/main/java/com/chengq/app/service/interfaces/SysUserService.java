package com.chengq.app.service.interfaces;

import com.chengq.api.model.SysUserAddRequest;
import com.chengq.api.model.SysUserUpdateRequest;
import com.chengq.api.model.SysUserVO;

import java.util.List;

public interface SysUserService {

    List<SysUserVO> listUsers();

    SysUserVO addUser(SysUserAddRequest request);

    SysUserVO updateUser(SysUserUpdateRequest request);

    void deleteUser(Long id);
}
