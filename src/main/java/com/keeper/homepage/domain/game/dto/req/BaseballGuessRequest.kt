package com.keeper.homepage.domain.game.dto.req

import com.keeper.homepage.domain.game.application.GUESS_NUMBER_LENGTH
import org.hibernate.validator.constraints.Length

data class BaseballGuessRequest(
    @field:Length(min = GUESS_NUMBER_LENGTH, max = GUESS_NUMBER_LENGTH)
    val guessNumber: String,
)
