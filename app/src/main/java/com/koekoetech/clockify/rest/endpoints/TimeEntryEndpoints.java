package com.koekoetech.clockify.rest.endpoints;

import com.koekoetech.clockify.models.TimeEntry;
import com.koekoetech.clockify.models.TimeEntryRecord;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TimeEntryEndpoints {

    @POST("workspaces/{workspaceId}/time-entries")
    Call<TimeEntry> postTimeEntry(@Path("workspaceId") String workspaceId, @Body TimeEntry timeEntry);

    @GET("workspaces/{workspaceId}/user/{userId}/time-entries")
    Call<List<TimeEntryRecord>> getTimeEntryRecordList(@Path("workspaceId") String workspaceId,
                                                       @Path("userId") String userId,
                                                       @Query("page") int page);

}
