package com.github.kerubistan.kerub.utils

import org.apache.shiro.SecurityUtils

/**
 * Get current shiro user - throws exception if no user
 */
fun currentUser() = SecurityUtils.getSubject()?.principal?.toString()
