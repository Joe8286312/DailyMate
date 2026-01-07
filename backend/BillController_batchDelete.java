// 在 BillController 中添加以下方法

/**
 * 批量删除账单（软删除）
 * @param request 包含 ids 列表的请求
 */
@PutMapping("/batch/delete")
public void batchDeleteBills(@RequestBody Map<String, List<Long>> request) {
    List<Long> ids = request.get("ids");
    if (ids != null && !ids.isEmpty()) {
        billService.batchDelete(ids);
    }
}

/**
 * 示例请求格式：
 * PUT /api/bill/batch/delete
 * Content-Type: application/json
 * 
 * {
 *   "ids": [1, 2, 3]
 * }
 */
