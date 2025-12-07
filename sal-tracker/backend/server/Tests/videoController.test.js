import { jest } from '@jest/globals';

// Mock fs/promises BEFORE importing the controller
const mockReaddir = jest.fn();
jest.unstable_mockModule('fs/promises', () => ({
    default: {
        readdir: mockReaddir,
        access: jest.fn(),
    },
    readdir: mockReaddir,
    access: jest.fn(),
}));

// Dynamic imports after mocking
const { default: handlers } = await import('../controllers/controller.js');

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
        mockReaddir.mockResolvedValue(['video1.mp4', 'video2.mp4']);

    await handlers.getVideos(mockReq, mockRes);

        expect(mockReaddir).toHaveBeenCalledWith('mock/path');
        expect(mockRes.json).toHaveBeenCalledWith(['video1.mp4', 'video2.mp4']);
    });

    it('should handle read directory errors', async () => {
        mockReaddir.mockRejectedValue(new Error('Directory read failed'));

    await handlers.getVideos(mockReq, mockRes);

        expect(mockRes.status).toHaveBeenCalledWith(500);
        expect(mockRes.json).toHaveBeenCalledWith({
            error: 'Failed to read video directory',
        });
    });
});
