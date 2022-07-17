package com.tencent.community.domain;


/**
 * page分页需要的信息
 */
public class Page {

    private Integer current = 1;

    private Integer limit = 10;



    /**
     * 数据库总条数
     */
    private Integer rows;

    private String path;


    /**
     * 不设置属性，thyemleaf可调用相关的get，set方法
     * @return
     */
//    /**
//     * 起始页页号
//     */
//    private Integer from;
//
//    /**
//     * 终止页页号
//     */
//    private Integer to;
//    /**
//     //     * select语句中的起始行数
//     //     */
////    private Integer start;
//    //
//
//    /**
//     * 总页数
//     */
//    private Integer total;

    public Integer getStart() {
        return (current - 1) * limit;
    }


    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }


    public Page() {
    }

    public Integer getCurrent() {
        return current;
    }

    public void setCurrent(Integer current) {
        if(current > 1){
            this.current = current;
        }
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        if(limit > 1 && limit < 100){
            this.limit = limit;
        }

    }

    public Integer getTotal() {
        int n = rows % limit;
        if(n == 0){
            return n;
        }else{
            return n + 1;
        }
    }



    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Integer getFrom() {
        return Math.max((current - 2), 1);
    }


    public Integer getTo() {
        return Math.min(current + 2, getTotal());
    }


    @Override
    public String toString() {
        return "Page{" +
                "current=" + current +
                ", limit=" + limit +
                ", rows=" + rows +
                ", path='" + path + '\'' +
                '}';
    }
}
