package pocketbudget.infra.persistence.inMemory;

import pocketbudget.domain.user.User;
import pocketbudget.domain.user.UserRepository;

import java.util.*;

public class InMemoryUserRepository implements UserRepository {
    private final Map<String, User> store = new HashMap<>();

    @Override
    public void save(User user) {
        store.put(user.getUsername(), user);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(store.get(username));
    }

    @Override
    public boolean existsByUsername(String username) {
        return store.containsKey(username);
    }
}
