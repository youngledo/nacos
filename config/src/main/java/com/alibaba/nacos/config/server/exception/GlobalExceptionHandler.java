/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.nacos.config.server.exception;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.exception.runtime.NacosRuntimeException;
import com.alibaba.nacos.common.utils.ExceptionUtil;
import com.alibaba.nacos.config.server.monitor.MetricsMonitor;
import com.alibaba.nacos.persistence.monitor.DatasourceMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Global exception handler.
 *
 * @author Nacos
 */
@ControllerAdvice(basePackages = "com.alibaba.nacos.config.server")
@Order(Ordered.LOWEST_PRECEDENCE - 1)
public class GlobalExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    /**
     * For IllegalArgumentException, we are returning void with status code as 400, so our error-page will be used in
     * this case.
     *
     * @throws IllegalArgumentException IllegalArgumentException.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(Exception ex) throws IOException {
        MetricsMonitor.getIllegalArgumentException().increment();
        LOGGER.error("IllegalArgumentException occurred: {}", ex.getMessage(), ex);
        return ResponseEntity.status(400).body(ExceptionUtil.getAllExceptionMsg(ex));
    }
    
    /**
     * For NacosRuntimeException.
     *
     * @throws NacosRuntimeException NacosRuntimeException.
     */
    @ExceptionHandler(NacosRuntimeException.class)
    public ResponseEntity<String> handleNacosRunTimeException(NacosRuntimeException ex) throws IOException {
        MetricsMonitor.getNacosException().increment();
        LOGGER.error("NacosRuntimeException occurred: {}", ex.getMessage(), ex);
        return ResponseEntity.status(ex.getErrCode()).body(ExceptionUtil.getAllExceptionMsg(ex));
    }

    /**
     * For NacosException.
     *
     * @throws NacosException NacosException.
     */
    @ExceptionHandler(NacosException.class)
    public ResponseEntity<String> handleNacosException(NacosException ex) throws IOException {
        MetricsMonitor.getNacosException().increment();
        LOGGER.error("NacosException occurred: {}", ex.getMessage(), ex);
        return ResponseEntity.status(ex.getErrCode()).body(ExceptionUtil.getAllExceptionMsg(ex));
    }

    /**
     * For DataAccessException.
     *
     * @throws DataAccessException DataAccessException.
     */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<String> handleDataAccessException(DataAccessException ex) throws DataAccessException {
        DatasourceMetrics.getDbException().increment();
        LOGGER.error("DataAccessException occurred: {}", ex.getMessage(), ex);
        return ResponseEntity.status(500).body(ExceptionUtil.getAllExceptionMsg(ex));
    }


    /**
     * Handle all other exceptions.
     *
     * @param ex      Exception
     * @param request HttpServletRequest
     * @return ResponseEntity with error message
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex, HttpServletRequest request) {
        LOGGER.error("Unexpected exception occurred: {}", ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .body(ExceptionUtil.getAllExceptionMsg(ex));
    }
}
