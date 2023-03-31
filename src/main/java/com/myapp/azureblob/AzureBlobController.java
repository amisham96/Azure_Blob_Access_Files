package com.myapp.azureblob;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.OffsetDateTime;
import java.util.Collections;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.azure.core.credential.TokenCredential;
import com.azure.core.http.rest.Response;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.azure.identity.DefaultAzureCredential;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobAccessPolicy;
import com.azure.storage.blob.models.BlobSignedIdentifier;
import com.azure.storage.blob.sas.BlobContainerSasPermission;
import com.azure.storage.blob.sas.BlobSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;

@RestController
public class AzureBlobController {

	@GetMapping("/call")
	public void assignBlob() throws IOException {

		TokenCredential credential = new ClientSecretCredentialBuilder()
				.clientId("<client_id>")
				.clientSecret("<client_secret>")
				.tenantId("<tenant_id>").build();
		String connectionString = "DefaultEndpointsProtocol=https;AccountName=<storage_account_name>;AccountKey=<storage_account_key>;EndpointSuffix=core.windows.net";
		BlobContainerClient containerClient = new BlobContainerClientBuilder().credential(credential)
				.connectionString(connectionString).containerName("<container_name>").buildClient();

		BlobSignedIdentifier identifier = new BlobSignedIdentifier().setId("9133a7d4-a185-4d45-9733-ab161cf75a88")
				.setAccessPolicy(new BlobAccessPolicy().setStartsOn(OffsetDateTime.now())
						.setExpiresOn(OffsetDateTime.now().plusDays(7)).setPermissions("rw"));

		// Set the access policy for the container
		containerClient.setAccessPolicy(null, Collections.singletonList(identifier));

	}

}
