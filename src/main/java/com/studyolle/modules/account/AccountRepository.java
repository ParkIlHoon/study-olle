package com.studyolle.modules.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * <h1>Account 리포지토리</h1>
 */
@Transactional(readOnly = true)
public interface AccountRepository extends JpaRepository<Account, Long>
{
    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    Account findByEmail(String email);

    Account findByNickname(String emailOrNick);
}