package com.example.demo.repositories;

import com.example.demo.entity.Order;
import com.example.demo.entity.OrderStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {

	@Query("SELECT o FROM Order o WHERE MONTH(o.createdAt) = :month AND YEAR(o.createdAt) = :year AND o.status = :status")
	List<Order> findSuccessfulOrdersByMonthAndYear(int month, int year, OrderStatus status);

	@Query("SELECT o FROM Order o WHERE DATE(o.createdAt) = :date AND o.status = :status")
	List<Order> findSuccessfulOrdersByDate(LocalDate date, OrderStatus status);

	@Query("SELECT o FROM Order o WHERE YEAR(o.createdAt) = :year AND o.status = :status")
	List<Order> findSuccessfulOrdersByYear(int year, OrderStatus status);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.status = :status")
    BigDecimal calculateOverallBusiness(OrderStatus status);

    @Query("SELECT o FROM Order o WHERE o.status = :status")
    List<Order> findAllByStatus(OrderStatus status);
}
