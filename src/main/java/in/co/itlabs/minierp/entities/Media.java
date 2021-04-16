package in.co.itlabs.minierp.entities;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Media {

	private int id;
	private int studentId;

	private String fileName;
	private String fileMime;
	private byte[] fileBytes;

	private String remark;
	private LocalDateTime createdAt;

	public boolean isImage() {
		boolean isImage = false;
		if (fileMime.equalsIgnoreCase("image/jpeg") || fileMime.equalsIgnoreCase("image/png")) {
			isImage = true;
		}
		return isImage;
	}

	public void clear() {
		id = 0;
		studentId = 0;
		fileName = null;
		fileMime = null;
		fileBytes = null;
		remark = null;
		createdAt = null;
	}
}