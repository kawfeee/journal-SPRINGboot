package com.journal.journalApp.repository;

import com.journal.journalApp.entity.JournalEntry;
import com.journal.journalApp.entity.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, ObjectId> {
    User findByUserName(String userName);

    void deleteByUserName(String userName);
}



//controller --> service --> repository