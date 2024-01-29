package com.sse.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.Date;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Export {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private long id;
  private String name;
  private String status;
  private Date lastUpdated;

  public Export(String name, String status, Date lastUpdated) {
    this.name = name;
    this.status = status;
    this.lastUpdated = lastUpdated;
  }
}
