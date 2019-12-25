package com.example.ISAums.model;

import com.example.ISAums.model.enumeration.InvitationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "friendship")
@Where(clause = "is_deleted='false'")
public class Friendship extends BaseEntity {

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_sender_id")
	@NotNull
	private User sender;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_invited_id")
	@NotNull
	private User invitedUser;

	@Column(name = "invitation_status")
	@Enumerated(EnumType.STRING)
	private InvitationStatus invitationStatus;
}
