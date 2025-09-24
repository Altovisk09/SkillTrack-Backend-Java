package com.eric.skilltrack.service;

import com.eric.skilltrack.model.User;

import java.io.IOException;

public interface McgService {
    void registerTrainer(User user) throws IOException;
}
