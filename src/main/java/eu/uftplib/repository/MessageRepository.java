package eu.uftplib.repository;

import eu.uftplib.entity.Message;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    Optional<Message> findById(Long id);

    @Query("SELECT m FROM Message m WHERE m.successfullSend = false and m.retryCount < ?1")
    List<Message> findRetryMessages(Long retryCount);
}
