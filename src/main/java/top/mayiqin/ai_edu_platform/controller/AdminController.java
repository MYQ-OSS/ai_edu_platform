package top.mayiqin.ai_edu_platform.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.mayiqin.ai_edu_platform.annotation.RequireAdmin;
import top.mayiqin.ai_edu_platform.constant.MessageConstant;
import top.mayiqin.ai_edu_platform.entity.dto.*;
import top.mayiqin.ai_edu_platform.entity.po.DictData;
import top.mayiqin.ai_edu_platform.entity.vo.QuestionListVO;
import top.mayiqin.ai_edu_platform.entity.vo.UserListVO;
import top.mayiqin.ai_edu_platform.exception.Result;
import top.mayiqin.ai_edu_platform.service.DictDataService;
import top.mayiqin.ai_edu_platform.service.QuestionService;
import top.mayiqin.ai_edu_platform.service.UserService;

/**
 * 后台管理控制器
 *
 * @author m'y'q
 */
@RestController
@RequestMapping("/admin")
@Slf4j
@Tag(name = "后台管理", description = "管理员专用接口")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private DictDataService dictDataService;

    /**
     * 查询用户列表（分页）
     *
     * @param queryDTO 查询条件（页码、每页条数、用户名、状态、角色）
     * @return 分页用户列表
     */
    @Operation(
            summary = "查询用户列表",
            description = "后台管理员查询用户列表，支持分页和条件筛选。需要管理员权限。"
    )
    @GetMapping("/user/list")
    @RequireAdmin("查询用户列表")
    public Result<Page<UserListVO>> getUserList(@Valid UserListQueryDTO queryDTO) {
        log.info("后台查询用户列表请求: pageNum={}, pageSize={}", queryDTO.getPageNum(), queryDTO.getPageSize());

        try {
            Page<UserListVO> page = userService.getUserList(queryDTO);
            return Result.success(MessageConstant.OPERATION_SUCCESS, page);
        } catch (Exception e) {
            log.error("查询用户列表失败: {}", e.getMessage(), e);
            return Result.error(500, "查询用户列表失败: " + e.getMessage());
        }
    }

    /**
     * 禁用/启用用户
     *
     * @param userId 用户ID
     * @param dto    状态更新DTO
     * @return 操作结果
     */
    @Operation(
            summary = "禁用/启用用户",
            description = "管理员禁用或启用指定用户账号。需要管理员权限。不能操作自己的账号。"
    )
    @PutMapping("/user/status/{userId}")
    @RequireAdmin("禁用/启用用户")
    public Result<Void> updateUserStatus(
            @PathVariable Long userId,
            @Valid @RequestBody UserStatusUpdateDTO dto
    ) {
        log.info("后台修改用户状态请求: userId={}, status={}", userId, dto.getStatus());

        try {
            userService.updateUserStatus(userId, dto.getStatus());
            String msg = "1".equals(dto.getStatus()) ? "禁用用户成功" : "启用用户成功";
            return Result.success(msg, null);
        } catch (Exception e) {
            log.error("修改用户状态失败: {}", e.getMessage(), e);
            return Result.error(500, "操作失败: " + e.getMessage());
        }
    }

    /**
     * 重置用户密码
     *
     * @param userId 用户ID
     * @return 操作结果
     */
    @Operation(
            summary = "重置用户密码",
            description = "管理员重置指定用户的密码为默认密码123456。需要管理员权限。不能重置自己的密码。"
    )
    @PutMapping("/user/password/reset/{userId}")
    @RequireAdmin("重置用户密码")
    public Result<Void> resetUserPassword(@PathVariable Long userId) {
        log.info("后台重置用户密码请求: userId={}", userId);

        try {
            userService.resetUserPassword(userId);
            return Result.success("密码重置成功，新密码为：123456", null);
        } catch (Exception e) {
            log.error("重置密码失败: {}", e.getMessage(), e);
            return Result.error(500, "重置密码失败: " + e.getMessage());
        }
    }

    /**
     * 新增基础题目
     *
     * @param dto 题目信息
     * @return 题目ID
     */
    @Operation(
            summary = "新增基础题目",
            description = "管理员手动新增基础题库题目，补充AI生成题库。需要管理员权限。"
    )
    @PostMapping("/question/add")
    @RequireAdmin("新增基础题目")
    public Result<Long> addQuestion(@Valid @RequestBody QuestionAddDTO dto) {
        log.info("后台新增题目请求: direction={}, targetSalary={}", dto.getDirection(), dto.getTargetSalary());

        try {
            Long questionId = questionService.addQuestion(dto);
            return Result.success(MessageConstant.QUESTION_ADD_SUCCESS, questionId);
        } catch (Exception e) {
            log.error("新增题目失败: {}", e.getMessage(), e);
            return Result.error(500, "新增题目失败: " + e.getMessage());
        }
    }

    /**
     * 编辑题目
     *
     * @param dto 题目信息
     * @return 操作结果
     */
    @Operation(
            summary = "编辑题目",
            description = "管理员编辑已有题目信息。需要管理员权限。"
    )
    @PutMapping("/question/update")
    @RequireAdmin("编辑题目")
    public Result<Void> updateQuestion(@Valid @RequestBody QuestionUpdateDTO dto) {
        log.info("后台编辑题目请求: id={}, direction={}", dto.getId(), dto.getDirection());

        try {
            questionService.updateQuestion(dto);
            return Result.success(MessageConstant.QUESTION_UPDATE_SUCCESS, null);
        } catch (Exception e) {
            log.error("编辑题目失败: {}", e.getMessage(), e);
            return Result.error(500, "编辑题目失败: " + e.getMessage());
        }
    }

    /**
     * 删除题目
     *
     * @param questionId 题目ID
     * @return 操作结果
     */
    @Operation(
            summary = "删除题目",
            description = "管理员删除指定题目（逻辑删除）。需要管理员权限。"
    )
    @DeleteMapping("/question/delete/{questionId}")
    @RequireAdmin("删除题目")
    public Result<Void> deleteQuestion(@PathVariable Long questionId) {
        log.info("后台删除题目请求: questionId={}", questionId);

        try {
            questionService.deleteQuestion(questionId);
            return Result.success(MessageConstant.QUESTION_DELETE_SUCCESS, null);
        } catch (Exception e) {
            log.error("删除题目失败: {}", e.getMessage(), e);
            return Result.error(500, "删除题目失败: " + e.getMessage());
        }
    }

    /**
     * 查询题目列表（分页）
     *
     * @param queryDTO 查询条件（页码、每页条数、题目名称、技术方向、目标薪资）
     * @return 分页题目列表
     */
    @Operation(
            summary = "查询题目列表",
            description = "后台管理员查询题目列表，支持分页和条件筛选。需要管理员权限。"
    )
    @GetMapping("/question/list")
    @RequireAdmin("查询题目列表")
    public Result<Page<QuestionListVO>> getQuestionList(@Valid QuestionListQueryDTO queryDTO) {
        log.info("后台查询题目列表请求: pageNum={}, pageSize={}", queryDTO.getPageNum(), queryDTO.getPageSize());

        try {
            Page<QuestionListVO> page = questionService.getQuestionList(queryDTO);
            return Result.success(MessageConstant.OPERATION_SUCCESS, page);
        } catch (Exception e) {
            log.error("查询题目列表失败: {}", e.getMessage(), e);
            return Result.error(500, "查询题目列表失败: " + e.getMessage());
        }
    }

    /**
     * 新增字典数据
     *
     * @param dto 字典数据信息
     * @return 字典数据ID
     */
    @Operation(
            summary = "新增字典数据",
            description = "管理员新增数据字典项（技术方向/学历/身份等）。需要管理员权限。"
    )
    @PostMapping("/dict/add")
    @RequireAdmin("新增字典数据")
    public Result<Long> addDictData(@Valid @RequestBody DictDataAddDTO dto) {
        log.info("后台新增字典数据请求: dictType={}, dictCode={}", dto.getDictType(), dto.getDictCode());

        try {
            Long dictId = dictDataService.addDictData(dto);
            return Result.success(MessageConstant.DICT_ADD_SUCCESS, dictId);
        } catch (Exception e) {
            log.error("新增字典数据失败: {}", e.getMessage(), e);
            return Result.error(500, "新增字典数据失败: " + e.getMessage());
        }
    }

    /**
     * 更新字典数据
     *
     * @param dto 字典数据信息
     * @return 操作结果
     */
    @Operation(
            summary = "更新字典数据",
            description = "管理员更新数据字典项信息。需要管理员权限。"
    )
    @PutMapping("/dict/update")
    @RequireAdmin("更新字典数据")
    public Result<Void> updateDictData(@Valid @RequestBody DictDataUpdateDTO dto) {
        log.info("后台更新字典数据请求: id={}, dictCode={}", dto.getId(), dto.getDictCode());

        try {
            dictDataService.updateDictData(dto);
            return Result.success(MessageConstant.OPERATION_SUCCESS, null);
        } catch (Exception e) {
            log.error("更新字典数据失败: {}", e.getMessage(), e);
            return Result.error(500, "更新字典数据失败: " + e.getMessage());
        }
    }

    /**
     * 删除字典数据
     *
     * @param id 字典ID
     * @return 操作结果
     */
    @Operation(
            summary = "删除字典数据",
            description = "管理员删除指定字典数据（逻辑删除）。需要管理员权限。"
    )
    @DeleteMapping("/dict/delete/{id}")
    @RequireAdmin("删除字典数据")
    public Result<Void> deleteDictData(@PathVariable Long id) {
        log.info("后台删除字典数据请求: id={}", id);

        try {
            dictDataService.deleteDictData(id);
            return Result.success(MessageConstant.OPERATION_SUCCESS, null);
        } catch (Exception e) {
            log.error("删除字典数据失败: {}", e.getMessage(), e);
            return Result.error(500, "删除字典数据失败: " + e.getMessage());
        }
    }

    /**
     * 查询字典列表（分页）
     *
     * @param queryDTO 查询条件（页码、每页条数、字典名称）
     * @return 分页字典列表
     */
    @Operation(
            summary = "查询字典列表",
            description = "后台管理员查询字典列表，支持分页和条件筛选。需要管理员权限。"
    )
    @GetMapping("/dict/list")
    @RequireAdmin("查询字典列表")
    public Result<Page<DictData>> getDictList(@Valid DictListQueryDTO queryDTO) {
        log.info("后台查询字典列表请求: pageNum={}, pageSize={}", queryDTO.getPageNum(), queryDTO.getPageSize());

        try {
            Page<DictData> page = dictDataService.getDictList(queryDTO);
            return Result.success(MessageConstant.OPERATION_SUCCESS, page);
        } catch (Exception e) {
            log.error("查询字典列表失败: {}", e.getMessage(), e);
            return Result.error(500, "查询字典列表失败: " + e.getMessage());
        }
    }
}
