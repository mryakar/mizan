package me.yakar.mizan.account.web;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenAccountRequest(String ownerName, String currency) {}
