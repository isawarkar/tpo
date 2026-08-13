package com.fb.controller;

import java.math.BigInteger;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.hibernate.HibernateException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lowagie.text.pdf.codec.Base64;

import tpo.beans.Parent;
import tpo.dao.CommonDBBean;
import tpo.hibernate.Company;
import tpo.hibernate.HallTicket;
import tpo.hibernate.HallTicketConnect;
import tpo.imageservice.client.FileUploadUtility;
import tpo.util.IMAGECONS;
import tpo.util.TpoUtil;

@RestController
@RequestMapping("FresherBuddyAdminService")
public class FresherBuddyAdminService extends Parent {

	@Autowired
	CommonDBBean commonDBBean;
	
	@Autowired
	FileUploadUtility fileUploadUtility;

	@PostMapping(path = "/validateAdminLogin", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	public String validateAdminLogin(@RequestBody String json) {
		String response = null;
		JSONParser parser = new JSONParser();
		try {
			Object obj = parser.parse(json);
			JSONObject jsonObject = (JSONObject) obj;
			String userName = (String) jsonObject.get("userName");
			String password = (String) jsonObject.get("password");
			if (commonDBBean != null) {
				String role = commonDBBean.validateAdminLogin(userName, password);
				if (role != null) {
					jsonObject = new JSONObject();
					jsonObject.put("loginFlag", true);
					jsonObject.put("role", role);
					response = jsonObject.toJSONString();
				} else {
					jsonObject = new JSONObject();
					jsonObject.put("loginFlag", false);
					response = jsonObject.toJSONString();
				}
			}
		} catch (ParseException e) {

			e.printStackTrace();
		}
		return response;
	}

	@PostMapping(path = "/changePassword", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	public String changePassword(@RequestBody String json) {
		String response = null;
		JSONParser parser = new JSONParser();
		try {
			Object obj = parser.parse(json);
			JSONObject jsonObject = (JSONObject) obj;
			String userName = (String) jsonObject.get("userName");
			String password = (String) jsonObject.get("password");
			String newPassword = (String) jsonObject.get("newPassword");

			if (commonDBBean != null) {
				try {
					if (commonDBBean.changeAdminPassword(userName, password, newPassword)) {
						response = "SYour password is successfully changed!";
					} else {
						response = "EInvalid Password!";
					}
				} catch (HibernateException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		} catch (ParseException e) {

			e.printStackTrace();
		}
		return response;
	}

	@PostMapping(path = "/forgotPassword", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	public String forgotPassword(@RequestBody String json) {
		String response = null;
		JSONParser parser = new JSONParser();
		try {
			Object obj = parser.parse(json);
			JSONObject jsonObject = (JSONObject) obj;
			String userName = (String) jsonObject.get("userName");
			String email = (String) jsonObject.get("email");
			String basePath = (String) jsonObject.get("BASE_PATH");

			if (commonDBBean != null) {
				try {
					if (commonDBBean.sendAdminPassword(userName, email, basePath)) {
						response = "SPassword Reset email has been send on your email!";
					} else {
						response = "EInvalid userName No or Email!";
					}
				} catch (HibernateException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		} catch (ParseException e) {

			e.printStackTrace();
		}
		return response;
	}

	@PostMapping(path = "/studentArrived", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	public String studentArrived(@RequestBody String json) {
		String response = null;
		try {
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(json);
			JSONObject jsonObject = (JSONObject) obj;
			String signature = (String) jsonObject.get("signature");
			String[] str = signature.split("#");
			if (str != null && str.length == 4) {
				String enrollmentNo = str[0];
				// JSONArray filesJson = new JSONArray();
				String digitalSignature = str[1] + "#" + str[2] + "#" + str[3];
				if (commonDBBean != null) {
					jsonObject = new JSONObject();
					int hallTicketNo = Integer.valueOf(str[1]);
					jsonObject.put("status",
							commonDBBean.verifyHallTicketStatus(enrollmentNo, hallTicketNo, digitalSignature));
					List<BigInteger> list = commonDBBean.getCountList(hallTicketNo);
					int totalShotListed = list.get(0).intValue();
					int totalApplied = list.get(1).intValue();
					int totalApproved = list.get(2).intValue();
					int totalArrived = list.get(3).intValue();
					jsonObject.put("totalShotListed", totalShotListed);
					jsonObject.put("totalApplied", totalApplied);
					jsonObject.put("totalApproved", totalApproved);
					jsonObject.put("totalArrived", totalArrived);
					putProfileImage(jsonObject, enrollmentNo);
				} else {
					jsonObject.put("status", "E");
				}
				response = jsonObject.toJSONString();
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return response;
	}

	@PostMapping(path = "/downloadAdminProfileImage", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	public String downloadAdminProfileImage(@RequestBody String json) {
		String response = null;
		JSONParser parser = new JSONParser();
		try {
			Object obj = parser.parse(json);
			JSONObject jsonObject = (JSONObject) obj;
			String userName = (String) jsonObject.get("userName");
			if (userName != null) {
				byte[] profileImage  = fileUploadUtility.downloadFile(getImageServiceUrl() + "/downloadImage", userName,
								IMAGECONS.userprofilepics);
				// JSONArray filesJson = new JSONArray();
				if(profileImage == null || profileImage.length == 0) {
					profileImage = TpoUtil.getNABytes();
				}
				
				String base64EncodedData = Base64.encodeBytes(profileImage);
				JSONObject fileJSON = new JSONObject();
				fileJSON.put("profileImage", base64EncodedData);
				response = fileJSON.toJSONString();
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return response;
	}

	@PostMapping(path = "/oppeningList", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	public String oppeningList(@RequestBody String json) {
		String response = null;
		try {
			if (commonDBBean != null) {
				JSONParser parser = new JSONParser();
				Object obj = parser.parse(json);
				JSONObject jsonObject = (JSONObject) obj;
				String userName = (String) jsonObject.get("userName");
				List<HallTicket> list = commonDBBean.getOppeningList(userName);
				JSONArray eventList = new JSONArray();

				if (list != null && list.size() > 0) {
					eventList.addAll(list);
					response = eventList.toJSONString();
				}
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
		return response;
	}

	@PostMapping(path = "/companyList", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	public String companyList(@RequestBody String json) {
		String response = null;
		try {
			if (commonDBBean != null) {
				JSONParser parser = new JSONParser();
				Object obj = parser.parse(json);
				JSONObject jsonObject = (JSONObject) obj;
				String userName = (String) jsonObject.get("userName");
				List<Company> list = commonDBBean.getCompanyListForAdmin(userName);
				JSONArray eventList = new JSONArray();
				if (list != null && list.size() > 0) {
					for(Company company : list) {
						company.setLogo(commonDBBean.getCompanyePic(String.valueOf(company.getCompanyID())));
					}
					eventList.addAll(list);
					response = eventList.toJSONString();
				}
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
		return response;
	}

	@PostMapping(path = "/aplliedListForCompany", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	public String aplliedListForCompany(@RequestBody String json) {
		String response = null;
		try {
			if (commonDBBean != null) {
				JSONParser parser = new JSONParser();
				Object obj = parser.parse(json);
				JSONObject jsonObject = (JSONObject) obj;
				String companyID = (String) jsonObject.get("companyID");
				List<HallTicket> list = commonDBBean.aplliedListForCompany(companyID);
				JSONArray hallTicketList = new JSONArray();
				if (list != null && list.size() > 0) {
					for (HallTicket hall : list) {
						BigInteger totalApplied = commonDBBean.getHallTicketAppliedCount(hall.getHallTicketId());
						BigInteger totalApproved = commonDBBean.getHallTicketApprovedCount(hall.getHallTicketId());
						BigInteger totalArrived = commonDBBean.getHallTicketArrivedCount(hall.getHallTicketId());
						BigInteger totalShortlisted = commonDBBean
								.getHallTicketShortListedCount(hall.getHallTicketId());
						hall.setTotalApplied(totalApplied.intValue());
						hall.setTotalApproved(totalApproved.intValue());
						hall.setTotalArrived(totalArrived.intValue());
						hall.setTotalShortListed(totalShortlisted.intValue());
						hallTicketList.add(hall);
					}
					response = hallTicketList.toJSONString();
				}
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
		return response;
	}

	@PostMapping(path = "/hallticketListByID", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	public String hallticketListByID(@RequestBody String json) {
		String response = null;
		try {
			if (commonDBBean != null) {
				JSONParser parser = new JSONParser();
				Object obj = parser.parse(json);
				JSONObject jsonObject = (JSONObject) obj;
				String hallticketId = (String) jsonObject.get("hallticketId");
				List<HallTicketConnect> list = commonDBBean.getHallTicketListByID(hallticketId);
				JSONArray hallTicketList = new JSONArray();
				if (list != null && list.size() > 0) {
					hallTicketList.addAll(list);
					response = hallTicketList.toJSONString();
				}
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
		return response;
	}

	@PostMapping(path = "/approveOrReject", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	public String approveOrReject(@RequestBody String json) {
		String response = null;
		try {
			if (commonDBBean != null) {
				JSONParser parser = new JSONParser();
				Object obj = parser.parse(json);
				JSONObject jsonObject = (JSONObject) obj;
				Boolean status = (Boolean) Boolean.valueOf(jsonObject.get("status").toString());
				String rollNumber = (String) jsonObject.get("rollNumber");
				Integer hallticketId = (Integer) Long.valueOf(jsonObject.get("hallticketId").toString()).intValue();
				Boolean flag = commonDBBean.sendEmail(status, rollNumber, hallticketId);
				if (flag) {
					response = "SSuccessfully Changed!";
				} else {
					response = "EError while updating...";
				}
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
		return response;
	}

	@PostMapping(path = "/hallTicketList", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	public String hallTicketList(@RequestBody String json) {
		String response = null;
		try {
			if (commonDBBean != null) {
				JSONParser parser = new JSONParser();
				Object obj = parser.parse(json);
				JSONObject jsonObject = (JSONObject) obj;
				String hallticketId = (String) jsonObject.get("hallticketId");
				List<HallTicketConnect> list = commonDBBean.getHallTicketListByID(hallticketId);
				JSONArray eventList = new JSONArray();

				if (list != null && list.size() > 0) {
					for (HallTicketConnect hallTicketObj : list) {
						eventList.add(hallTicketObj);
					}
					response = eventList.toJSONString();
				}
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
		return response;
	}

	@PostMapping(path = "/uploadProfileImage", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	public String uploadProfileImage(@RequestBody String json) {
		String response = null;
		JSONParser parser = new JSONParser();
		try {
			Object obj = parser.parse(json);
			JSONObject jsonObject = (JSONObject) obj;
			String registeredUserName = (String) jsonObject.get("registeredUserName");
			String encodedFile = (String) jsonObject.get("encodedFile");
			if (registeredUserName != null && encodedFile != null) {
				fileUploadUtility.uploadFileWithByteArray(getImageServiceUrl() + "/upload", registeredUserName,
						Base64.decode(encodedFile), IMAGECONS.userprofilepics);
				JSONObject fileJSON = new JSONObject();
				fileJSON.put("code", "success");
				response = fileJSON.toJSONString();
			}
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	@PostMapping(path = "/deleteCompany", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	public String deleteCompany(@RequestBody String json) {
		String response = null;
		JSONParser parser = new JSONParser();
		try {
			Object obj = parser.parse(json);
			JSONObject jsonObject = (JSONObject) obj;
			String companyId = (String) jsonObject.get("companyId");
			if (companyId != null) {
				if (commonDBBean.deleteCompany(companyId)) {
					response = "SSuccessfully deleted!";
				} else {
					response = "ERequest does not exist for  " + companyId + "!";
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	@PostMapping(path = "/deleteOpening", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	public String deleteOpening(@RequestBody String json) {
		String response = null;
		JSONParser parser = new JSONParser();
		try {
			Object obj = parser.parse(json);
			JSONObject jsonObject = (JSONObject) obj;
			String hallticketId = (String) jsonObject.get("hallticketId");
			if (hallticketId != null) {
				if (commonDBBean.deleteOpening(hallticketId)) {
					response = "SSuccessfully deleted!";
				} else {
					response = "ERequest does not exist for  " + hallticketId + "!";
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	private void putProfileImage(JSONObject jsonObject, String enrollmentNo) {
		String base64EncodedData = Base64.encodeBytes(commonDBBean.getStudentProfilePic(enrollmentNo));
		jsonObject.put("profileImage", base64EncodedData);
	}
}