package org.wixpress.fjarr.validation;

import org.springframework.validation.Errors;

/**
 * @author alex
 * @since 12/23/12 10:29 PM
 */

public class ValidationException extends RuntimeException
{
    final Errors errors;

    public ValidationException(Errors errors)
    {
        super("Validation failed");
        this.errors = errors;
    }
}
