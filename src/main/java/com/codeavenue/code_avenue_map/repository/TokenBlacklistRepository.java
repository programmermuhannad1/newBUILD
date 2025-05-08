package com.codeavenue.code_avenue_map.repository;

import org.springframework.stereotype.Repository;
import java.util.HashSet;
import java.util.Set;

@Repository
public class TokenBlacklistRepository {

    private final Set<String> blacklist = new HashSet<>();

    public void addToBlacklist(String token) {
        blacklist.add(token);
    }

    public boolean isBlacklisted(String token) {
        return blacklist.contains(token);
    }

}
