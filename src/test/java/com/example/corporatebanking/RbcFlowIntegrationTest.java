//package com.example.corporatebanking;
//
//import com.example.rbcaccess.exception.AccessDeniedException;
//import com.example.rbcaccess.exception.ApprovalLimitExceededException;
//import com.example.rbcaccess.exception.SelfApprovalNotAllowedException;
//import com.example.rbcaccess.exception.TransactionLimitExceededException;
//import com.example.rbcaccess.service.RbcAccessService;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.math.BigDecimal;
//
//import static org.junit.jupiter.api.Assertions.*;
//
///**
// * End-to-end style tests that exercise the injected rbc-access library
// * exactly the way FundTransferService / ApprovalService do, proving the
// * whole "three Maven projects wired together" architecture works.
// */
//@SpringBootTest
//class RbcFlowIntegrationTest {
//
//    @Autowired
//    private RbcAccessService rbcAccessService;
//
//    @Test
//    void contextLoads() {
//        assertNotNull(rbcAccessService);
//    }
//
//    @Test
//    void maker1CanCreateBeneficiaryButNotFundTransfer() {
//        assertDoesNotThrow(() -> rbcAccessService.checkAccess("maker1", "CREATE_BENEFICIARY", null, null));
//        assertThrows(AccessDeniedException.class,
//                () -> rbcAccessService.checkAccess("maker1", "CREATE_FUND_TRANSFER", BigDecimal.valueOf(100), null));
//    }
//
//    @Test
//    void maker2IsLimitedTo1000() {
//        assertDoesNotThrow(() -> rbcAccessService.checkAccess("maker2", "CREATE_FUND_TRANSFER", BigDecimal.valueOf(500), null));
//        assertDoesNotThrow(() -> rbcAccessService.checkAccess("maker2", "CREATE_FUND_TRANSFER", BigDecimal.valueOf(1000), null));
//        assertThrows(TransactionLimitExceededException.class,
//                () -> rbcAccessService.checkAccess("maker2", "CREATE_FUND_TRANSFER", BigDecimal.valueOf(1500), null));
//    }
//
//    @Test
//    void checker1IsLimitedTo1000Approval() {
//        assertDoesNotThrow(() -> rbcAccessService.checkAccess("checker1", "APPROVE_FUND_TRANSFER", BigDecimal.valueOf(1000), "maker2"));
//        assertThrows(ApprovalLimitExceededException.class,
//                () -> rbcAccessService.checkAccess("checker1", "APPROVE_FUND_TRANSFER", BigDecimal.valueOf(1500), "maker2"));
//    }
//
//    @Test
//    void stpCannotApproveItsOwnTransaction() {
//        assertThrows(SelfApprovalNotAllowedException.class,
//                () -> rbcAccessService.checkAccess("stp", "APPROVE_FUND_TRANSFER", BigDecimal.valueOf(100), "stp"));
//    }
//
//    @Test
//    void stpCanApproveAnotherUsersTransaction() {
//        assertDoesNotThrow(() ->
//                rbcAccessService.checkAccess("stp", "APPROVE_FUND_TRANSFER", BigDecimal.valueOf(100), "maker2"));
//    }
//}
