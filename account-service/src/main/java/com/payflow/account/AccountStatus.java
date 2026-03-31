package com.payflow.account;

/**
 * An enum is a fixed set of constants.
 * A bank account can only be in one of these states.
 * Using an enum prevents bugs — you can't accidentally
 */
public enum AccountStatus {
    ACTIVE,
    INACTIVE,
    SUSPENDED
}