package com.metanetglobal.knowledge.worker.common.bean;

/**
 * Common Code DTO
 *
 * @author      namki.an
 * @version     1.0.0
 */
public class CommCodeDTO {
    private String codeId;
    private String codeName;
    private String markName;

    public String getCodeId() {
        return codeId;
    }
    public void setCodeId(String codeId) {
        this.codeId = codeId;
    }
    public String getCodeName() {
        return codeName;
    }
    public void setCodeName(String codeName) {
        this.codeName = codeName;
    }
    public String getMarkName() {
        return markName;
    }
    public void setMarkName(String markName) {
        this.markName = markName;
    }
    public void removeAll() {
        codeId = null;
        codeName = null;
        markName = null;
    }
}
