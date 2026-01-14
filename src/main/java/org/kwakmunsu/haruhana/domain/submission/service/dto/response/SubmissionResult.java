package org.kwakmunsu.haruhana.domain.submission.service.dto.response;

import org.kwakmunsu.haruhana.domain.submission.entity.Submission;

public record SubmissionResult(
        Submission submission,
        boolean isFirstSubmission
) {

}