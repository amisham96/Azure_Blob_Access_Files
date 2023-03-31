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

	@PostMapping("/upload")
	public void uploadFile(@RequestParam(value = "file") MultipartFile file) throws IOException {

		// Code To Create and File In Blob Storage
		String str = "DefaultEndpointsProtocol=https;AccountName=<storage_account_name>;AccountKey=<storage_account_key>;EndpointSuffix=core.windows.net";

		OffsetDateTime expiryTime = OffsetDateTime.now().plusDays(1);
		BlobSasPermission permission = new BlobSasPermission().setReadPermission(true);
		BlobServiceSasSignatureValues values = new BlobServiceSasSignatureValues(expiryTime, permission)
				.setStartTime(OffsetDateTime.now());
		BlobContainerClient container = new BlobContainerClientBuilder().connectionString(str)
				.containerName("<conatiner_name>").buildClient();

		BlobClient blob = container.getBlobClient(file.getOriginalFilename());
		blob.upload(file.getInputStream(), file.getSize(), true);
		String sasToken = blob.generateSas(values);
		// Code To Create and File In Blob Storage

		// Code To download the File From Blob Storage
		URL url = new URL(blob.getBlobUrl() + "?" + sasToken);

		HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
		int responseCode = httpConn.getResponseCode();

		// Check if the response code is HTTP_OK (200)
		if (responseCode == HttpURLConnection.HTTP_OK) {
			// Open input stream from the HTTP connection
			InputStream inputStream = httpConn.getInputStream();

			// Open output stream to save the file
			FileOutputStream outputStream = new FileOutputStream("<path_to_download>");

			// Read bytes from input stream and write to output stream
			int bytesRead;
			byte[] buffer = new byte[4096];
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
			}

			// Close streams
			outputStream.close();
			inputStream.close();
			System.out.println("File downloaded");
		} else {
			System.out.println("Failed to download file: " + httpConn.getResponseMessage());
		}
		httpConn.disconnect();
		// Code To download the File From Blob Storage
	}

//	@GetMapping("/call")
//	public void assignBlob() throws IOException {
//
//		TokenCredential credential = new ClientSecretCredentialBuilder()
//				.clientId("<client_id>")
//				.clientSecret("<client_secret>")
//				.tenantId("<tenant_id>").build();
//		String connectionString = "DefaultEndpointsProtocol=https;AccountName=<storage_account_name>;AccountKey=<storage_account_key>;EndpointSuffix=core.windows.net";
//		BlobContainerClient containerClient = new BlobContainerClientBuilder().credential(credential)
//				.connectionString(connectionString).containerName("<container_name>").buildClient();
//
//		BlobSignedIdentifier identifier = new BlobSignedIdentifier().setId("9133a7d4-a185-4d45-9733-ab161cf75a88")
//				.setAccessPolicy(new BlobAccessPolicy().setStartsOn(OffsetDateTime.now())
//						.setExpiresOn(OffsetDateTime.now().plusDays(7)).setPermissions("rw"));
//
//		// Set the access policy for the container
//		containerClient.setAccessPolicy(null, Collections.singletonList(identifier));
//
//	}

}
