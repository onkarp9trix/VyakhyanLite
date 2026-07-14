package com.navaantrix.vyakhyanLite.repository;

import com.navaantrix.vyakhyanLite.entity.Dashboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DashboardRepository extends JpaRepository<Dashboard, Long> {

    Optional<Dashboard> findByDashboardNameIgnoreCaseAndConversationConversationIdAndStatusStatusIdNot(String dashboardName, Long conversationId, Long deleteStatus);

    List<Dashboard> findByConversationConversationIdAndStatusStatusId(Long conversationId, Long activeStatus);

    Dashboard findByDashboardIdAndStatusStatusIdNot(Long dashBoardId, Long deleteStatus);
}
