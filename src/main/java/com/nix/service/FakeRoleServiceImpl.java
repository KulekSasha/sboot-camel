package com.nix.service;

import org.springframework.stereotype.Service;

@Service
public class FakeRoleServiceImpl implements FakeRoleService {
    @Override
    public long findIdByName(String roleName) {

        switch (roleName.toLowerCase()) {
            case "admin":
                return 1L;
            case "user":
                return 2L;
            default:
                return 0;
        }
    }
}
