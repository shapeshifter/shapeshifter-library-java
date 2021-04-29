// SPDX-FileCopyrightText: 2020-2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0

package eu.uftplib.repository;

import eu.uftplib.entity.Message;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    Optional<Message> findById(Long id);

    @Query("SELECT m FROM Message m WHERE m.successfullSend = false and m.incomming = false and m.outgoing = true and m.retryCount < ?1")
    List<Message> findRetryMessages(Long retryCount);

    @Modifying
    @Transactional
    @Query("UPDATE Message m set m.retryCount = ?2 where m.id = ?1")
    void setRetryCountById(Long id, Long retryCount);

    @Modifying
    @Transactional
    @Query("UPDATE Message m set m.successfullSend = ?2 where m.id = ?1")
    void setSuccessfullSendById(Long id, boolean successfullSend);
}
