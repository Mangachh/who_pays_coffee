package cbs.wantACoffe.repository;

import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import cbs.wantACoffe.dto.payment.IPaymentTotal;
import cbs.wantACoffe.entity.Payment;

@Repository
public interface IPaymentRepo extends JpaRepository<Payment, Long> {

        List<Payment> findAllByMemberMemberIdOrderByPaymentDateAsc(final Long memberId);

        List<Payment> findAllByMemberMemberIdAndPaymentDateBetweenOrderByPaymentDateAsc(final Long memberId,
                        final Date startDate, final Date endDate);

        List<Payment> findAllByGroupGroupIdOrderByPaymentDateAsc(final Long groupId);

        List<Payment> findAllByGroupGroupIdAndPaymentDateBetweenOrderByPaymentDateAsc(final Long groupId,
                        final Date startDate, final Date endDate);

        /*
         * SELECT member_name, SUM(amount), member_id FROM payments
         * WHERE group_id = id
         * GROUP BY member_name;
         */
        @Query(value = "SELECT " + Payment.COLUMN_MEMBER_PAYED_NAME_NAME + " AS nickname, " + "SUM("
                        + Payment.COLUMN_AMOUNT_NAME + ") AS \"totalAmount\", "
                        + Payment.COLUMN_MEMBER_PAYED_ID_NAME + " AS memberId" +
                        " FROM " + Payment.TABLE_NAME +
                        " WHERE " + Payment.COLUMN_GROUP_ID_NAME + " = :groupId" +
                        " GROUP BY " + Payment.COLUMN_MEMBER_PAYED_NAME_NAME, nativeQuery = true)
        List<IPaymentTotal> findAllTotalsByGroup(@Param("groupId") Long groupId);

        /*
         * SELECT member_name, SUM(amount), member_id FROM payments
         * WHERE group_id = id
         * AND payment_date BETWEEN startDate AND endDate
         * GROUP BY member_name;
         */
        @Query(value = "SELECT " + Payment.COLUMN_MEMBER_PAYED_NAME_NAME + " AS nickname, " + "SUM("
                        + Payment.COLUMN_AMOUNT_NAME + ") AS \"totalAmount\", "
                        + Payment.COLUMN_MEMBER_PAYED_ID_NAME + " AS memberId" +
                        " FROM " + Payment.TABLE_NAME +
                        " WHERE " + Payment.COLUMN_GROUP_ID_NAME + " = :groupId" +
                        " AND " + Payment.COLUMN_DATE_NAME + " BETWEEN :initDate AND :endDate" +
                        " GROUP BY " + Payment.COLUMN_MEMBER_PAYED_NAME_NAME, nativeQuery = true)
        List<IPaymentTotal> findAllTotalsByGroupBetweenDates(
                        @Param("groupId") Long groupId,
                        @Param("initDate") Date initDate,
                        @Param("endDate") Date endDate);

        @Query(value = "SELECT " + Payment.COLUMN_MEMBER_PAYED_NAME_NAME + " AS nickname, " + "SUM("
                        + Payment.COLUMN_AMOUNT_NAME + ") AS \"totalAmount\", "
                        + Payment.COLUMN_MEMBER_PAYED_ID_NAME + " AS memberId" +
                        " FROM " + Payment.TABLE_NAME +
                        " WHERE " + Payment.COLUMN_GROUP_ID_NAME + " = :groupId" +
                        " AND " + Payment.COLUMN_MEMBER_PAYED_NAME_NAME + " = :memberNickname" +
                        " GROUP BY " + Payment.COLUMN_MEMBER_PAYED_NAME_NAME, nativeQuery = true)
        List<IPaymentTotal> findTotalsByMemberAndGroup(
                        @Param("groupId") Long groupId,
                        @Param("memberNickname") String memberNickname);

        /**
         * 
         * @param groupId
         * @param memberNickname
         * @param initDate
         * @param endDate
         * @return
         */
        @Query(value = "SELECT " + Payment.COLUMN_MEMBER_PAYED_NAME_NAME + " AS nickname, " + "SUM("
                        + Payment.COLUMN_AMOUNT_NAME + ") AS \"totalAmount\", "
                        + Payment.COLUMN_MEMBER_PAYED_ID_NAME + " AS memberId" +
                        " FROM " + Payment.TABLE_NAME +
                        " WHERE " + Payment.COLUMN_GROUP_ID_NAME + " = :groupId" +
                        " AND " + Payment.COLUMN_MEMBER_PAYED_NAME_NAME + " = :memberNickname" +
                        " AND " + Payment.COLUMN_DATE_NAME + " BETWEEN :initDate AND :endDate" +
                        " GROUP BY " + Payment.COLUMN_MEMBER_PAYED_NAME_NAME, nativeQuery = true)
        List<IPaymentTotal> findTotalsByMemberAndGroupBetweenDates(
                        @Param("groupId") Long groupId,
                        @Param("memberNickname") String memberNickname,
                        @Param("initDate") Date initDate,
                        @Param("endDate") Date endDate);
}
