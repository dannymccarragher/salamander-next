import handlers from '../controllers/controller.js';
import fs from 'fs/promises';

jest.mock('fs/promises');

describe('getVideos', () => {
  const mockReq = {};
  const mockRes = {
    json: jest.fn(),
    status: jest.fn().mockReturnThis(),
  };

  beforeEach(() => {
    jest.clearAllMocks();
    process.env.VIDEO_PATH = 'mock/path';
  });

  it('should return list of video files', async () => {
    fs.readdir.mockResolvedValue(['video1.mp4', 'video2.mp4']);

    await handlers.getVideos(mockReq, mockRes);

    expect(fs.readdir).toHaveBeenCalledWith('mock/path');
    expect(mockRes.json).toHaveBeenCalledWith(['video1.mp4', 'video2.mp4']);
  });

  it('should handle read directory errors', async () => {
    fs.readdir.mockRejectedValue(new Error('Directory read failed'));

    await handlers.getVideos(mockReq, mockRes);

    expect(mockRes.status).toHaveBeenCalledWith(500);
    expect(mockRes.json).toHaveBeenCalledWith({
      error: 'Failed to read video directory',
    });
    
  });
});
