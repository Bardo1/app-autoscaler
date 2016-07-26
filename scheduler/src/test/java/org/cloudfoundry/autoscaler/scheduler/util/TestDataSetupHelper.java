package org.cloudfoundry.autoscaler.scheduler.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.cloudfoundry.autoscaler.scheduler.entity.SpecificDateScheduleEntity;
import org.cloudfoundry.autoscaler.scheduler.rest.model.ApplicationScalingSchedules;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 *
 */
public class TestDataSetupHelper {
	private static List<String> genAppIds = new ArrayList<>();
	private static String timeZone = DateHelper.supportedTimezones[0];
	private static String invalidTimezone = "Invalid TimeZone";
	private static String startDate[] = { "2100-07-20", "2100-07-22", "2100-07-25", "2100-07-28", "2100-8-10" };
	private static String endDate[] = { "2100-07-20", "2100-07-23", "2100-07-27", "2100-08-07", "2100-8-10" };
	private static String startTime[] = { "08:00:00", "13:00:00", "09:00:00" };
	private static String endTime[] = { "10:00:00", "09:00:00", "09:00:00" };


	public static List<SpecificDateScheduleEntity> generateSpecificDateScheduleEntities(String appId,
			int noOfSpecificDateSchedulesToSetUp) {
		List<SpecificDateScheduleEntity> specificDateSchedules = generateSpecificDateScheduleEntities(appId, timeZone,
				noOfSpecificDateSchedulesToSetUp, false, 1, 5);

		return specificDateSchedules;
	}

	public static List<SpecificDateScheduleEntity> generateSpecificDateScheduleEntitiesWithCurrentStartEndTime(
			String appId,
			int noOfSpecificDateSchedulesToSetUp) {
		List<SpecificDateScheduleEntity> specificDateSchedules = generateSpecificDateScheduleEntities(appId, timeZone,
				noOfSpecificDateSchedulesToSetUp, true, 1, 5);

		return specificDateSchedules;
	}

	public static ApplicationScalingSchedules generateSpecificDateSchedules(String appId, int noOfSchedules) {
		ApplicationScalingSchedules schedules = new ApplicationScalingSchedules();
		List<SpecificDateScheduleEntity> specificDateSchedules = generateSpecificDateScheduleEntities(appId, timeZone,
				noOfSchedules, false, 1, 5);
		schedules.setSpecific_date(specificDateSchedules);
		return schedules;

	}

	public static ApplicationScalingSchedules generateSpecificDateSchedulesForScheduleController(String appId,
			int noOfSpecificDateSchedules) {
		ApplicationScalingSchedules schedules = new ApplicationScalingSchedules();
		schedules.setTimeZone(timeZone);
		schedules.setInstance_min_count(1);
		schedules.setInstance_max_count(5);
		List<SpecificDateScheduleEntity> specificDateSchedules = generateSpecificDateScheduleEntities(appId, null,
				noOfSpecificDateSchedules, false, null, null);
		schedules.setSpecific_date(specificDateSchedules);
		return schedules;

	}

	public static String generateJsonSchedule(String appId, int noOfSpecificDateSchedulesToSetUp,
			int noOfRecurringSchedulesToSetUp) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();

		ApplicationScalingSchedules schedules = new ApplicationScalingSchedules();
		schedules.setTimeZone(timeZone);
		schedules.setInstance_min_count(1);
		schedules.setInstance_max_count(5);
		schedules.setSpecific_date(
				generateSpecificDateScheduleEntities(null, null, noOfSpecificDateSchedulesToSetUp, false, null, null));

		return mapper.writeValueAsString(schedules);
	}

	private static List<SpecificDateScheduleEntity> generateSpecificDateScheduleEntities(String appId, String timeZone,
			int noOfSpecificDateSchedulesToSetUp, boolean setCurrentDateTime, Integer defaultInstanceMinCount,
			Integer defaultInstanceMaxCount) {
		if (noOfSpecificDateSchedulesToSetUp <= 0) {
			return null;
		}
		List<SpecificDateScheduleEntity> specificDateSchedules = new ArrayList<>();

		int pos = 0;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		for (int i = 0; i < noOfSpecificDateSchedulesToSetUp; i++) {
			SpecificDateScheduleEntity specificDateScheduleEntity = new SpecificDateScheduleEntity();
			specificDateScheduleEntity.setAppId(appId);
			specificDateScheduleEntity.setTimeZone(timeZone);

			try {
				if (setCurrentDateTime) {
					specificDateScheduleEntity.setStartDate(sdf.parse(getCurrentDate(0)));
					specificDateScheduleEntity.setEndDate(sdf.parse(getCurrentDate(0)));
					specificDateScheduleEntity.setStartTime(java.sql.Time.valueOf(getCurrentTime(0)));
					specificDateScheduleEntity.setEndTime(java.sql.Time.valueOf(getCurrentTime(0)));
				} else {
					specificDateScheduleEntity.setStartDate(sdf.parse(getDate(startDate, pos, 0)));
					specificDateScheduleEntity.setEndDate(sdf.parse(getDate(endDate, pos, 5)));
					specificDateScheduleEntity.setStartTime(java.sql.Time.valueOf(getTime(startTime, pos, 0)));
					specificDateScheduleEntity.setEndTime(java.sql.Time.valueOf(getTime(endTime, pos, 5)));
				}
			} catch (ParseException e) {
				throw new RuntimeException(e.getMessage());
			}

			specificDateScheduleEntity.setInstanceMinCount(i + 5);
			specificDateScheduleEntity.setInstanceMaxCount(i + 6);
			specificDateScheduleEntity.setDefaultInstanceMinCount(defaultInstanceMinCount);
			specificDateScheduleEntity.setDefaultInstanceMaxCount(defaultInstanceMaxCount);
			specificDateSchedules.add(specificDateScheduleEntity);
			pos++;
		}

		return specificDateSchedules;
	}

	private static String getDate(String[] date, int pos, int offsetMin) {
		if (date != null && date.length > pos) {
			return date[pos];
		} else {
			return getCurrentDate(offsetMin);
		}
	}

	private static String getTime(String[] time, int pos, int offsetMin) {
		if (time != null && time.length > pos) {
			return time[pos];
		} else {
			return getCurrentTime(offsetMin);
		}
	}

	private static String getCurrentTime(int offsetMin) {
		SimpleDateFormat sdfDate = new SimpleDateFormat("HH:mm:ss");
		Date now = new Date();
		now.setTime(now.getTime() + TimeUnit.MINUTES.toMillis(offsetMin));
		String strDate = sdfDate.format(now);
		return strDate;
	}

	private static String getCurrentDate(int offsetMin) {
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
		Date now = new Date();
		now.setTime(now.getTime() + TimeUnit.MINUTES.toMillis(offsetMin));
		String strDate = sdfDate.format(now);
		return strDate;
	}

	public static String getTimeZone() {
		return timeZone;
	}

	public static String[] generateAppIds(int noOfAppIdsToGenerate) {
		List<String> appIds = new ArrayList<>();
		for (int i = 0; i < noOfAppIdsToGenerate; i++) {
			UUID uuid = UUID.randomUUID();
			genAppIds.add(uuid.toString());
			appIds.add(uuid.toString());
		}
		return appIds.toArray(new String[0]);
	}

	public static List<String> getAllGeneratedAppIds() {
		return genAppIds;
	}

	public static String[] getStartDate() {
		return startDate;
	}

	public static String[] getEndDate() {
		return endDate;
	}

	public static String[] getStartTime() {
		return startTime;
	}

	public static String[] getEndTime() {
		return endTime;
	}

	public static String getInvalidTimezone() {
		return invalidTimezone;
	}

}