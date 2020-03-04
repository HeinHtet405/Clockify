package com.koekoetech.clockify.rest.endpoints;

import com.koekoetech.clockify.models.Project;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ProjectEndpoints {

    @GET("workspaces/{workspaceId}/projects")
    Call<List<Project>> getProjectList(@Path("workspaceId") String workspaceId,
                                       @Query("page-size") int pageSize);

}
