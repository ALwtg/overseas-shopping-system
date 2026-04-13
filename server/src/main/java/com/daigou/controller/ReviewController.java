package com.daigou.controller;

import com.daigou.common.PageResult;
import com.daigou.common.Result;
import com.daigou.entity.Review;
import com.daigou.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/review")
public class ReviewController {
    @Autowired private ReviewService reviewService;

    @GetMapping("/product/{productId}")
    public Result<PageResult<Review>> list(@PathVariable Long productId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size) {
        return Result.ok(reviewService.getByProduct(productId, page, size));
    }

    @PostMapping("/submit")
    public Result<?> submit(@RequestAttribute Long userId, @RequestBody Review review) {
        review.setUserId(userId);
        reviewService.submit(review);
        return Result.ok("评价成功");
    }
}
