package vn.java.dto.response;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
public abstract class PageResponseAbstract implements Serializable {
    protected int pageNumber;
    protected int pageSize;
    protected long totalElements;
    protected long totalPages;
}
