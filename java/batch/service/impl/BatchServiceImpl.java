/*
 * Copyright 2019 the original author or authors.
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
package com.hanwhalife.nbm.batch.service.impl;

import java.util.ArrayList;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hanwhalife.nbm.batch.dao.CommonBatchDAO;
import com.hanwhalife.nbm.batch.service.BatchService;

/**
 * <b>Description:</b><p>
 * 구현 클래스의 설명을 입력합니다.
 * @since 2019-07-29
 * @version 1.0
 * @author 1073302
 * @see
 * <pre>
 * 개정이력(Modification Information)
 * 수정일      수정자              수정내용
 * ----------  ---------           -------------------------------
 * 2019-07-29  1073302     최초생성
 * </pre>
 */
@Service
public class BatchServiceImpl implements BatchService {
  
  @Autowired
  CommonBatchDAO dao;
  
  @Override
  public ArrayList<Map<String, Object>> selectSample(Map<String, Object> paramMap) throws Exception {
    return (ArrayList<Map<String, Object>>) dao.selectList("batchMapper.CMCOD001L001B", paramMap);
  };
  @Override
  public ArrayList<Map<String, Object>> selectSample1(Map<String, Object> paramMap) throws Exception {
    return (ArrayList<Map<String, Object>>) dao.selectList("batchMapper.CMCOD001L002B", paramMap);
  };
}
