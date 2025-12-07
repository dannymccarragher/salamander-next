import { jest } from "@jest/globals";

// Force VIDEO_PATH for test
process.env.VIDEO_PATH = "mock/path";

// Mock fs/promises BEFORE import
jest.unstable_mockModule("fs/promises", () => ({
  readdir: jest.fn(),
}));

const fs = await import("fs/promises");
const { default: handlers } = await import("../controllers/controller.js");

describe("getVideos", () => {
  const mockReq = {};
  const mockRes = {
    json: jest.fn(),
    status: jest.fn().mockReturnThis(),
  };

  beforeEach(() => {
    jest.clearAllMocks();

    jest.spyOn(console, "error").mockImplementation(() => {});
  });

  it("should return list of video files", async () => {
    fs.readdir.mockResolvedValue(["video1.mp4", "video2.mp4"]);

    await handlers.getVideos(mockReq, mockRes);

    expect(fs.readdir).toHaveBeenCalledWith("mock/path");
    expect(mockRes.json).toHaveBeenCalledWith(["video1.mp4", "video2.mp4"]);
  });

  it("should handle read directory errors", async () => {
    fs.readdir.mockRejectedValue(new Error("Directory read failed"));

    await handlers.getVideos(mockReq, mockRes);

    expect(mockRes.status).toHaveBeenCalledWith(500);
    expect(mockRes.json).toHaveBeenCalledWith({
      error: "Failed to read video directory",
    });
  });
});